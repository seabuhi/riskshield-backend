package com.seabuhi.seacredit.module.ratelimit;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    // Each client IP gets its own bucket per rate-limited path
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only rate-limit sensitive endpoints
        if (!isRateLimited(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientIp(request) + ":" + path;
        Bucket bucket = buckets.computeIfAbsent(clientKey, k -> buildBucket(path));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            log.warn("Rate limit exceeded for IP={} path={}", getClientIp(request), path);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds));
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Çox sayda sorğu göndərdiniz. " +
                    waitSeconds + " saniyə gözləyin.\"}"
            );
        }
    }

    private boolean isRateLimited(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/signup")
                || path.startsWith("/otp/")
                || path.startsWith("/auth/forgot-password");
    }

    private Bucket buildBucket(String path) {
        Bandwidth limit;
        if (path.startsWith("/auth/login")) {
            // 5 login attempts per 1 minute
            limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        } else if (path.startsWith("/otp/")) {
            // 3 OTP requests per 5 minutes
            limit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(5)));
        } else {
            // 10 requests per minute for other sensitive endpoints
            limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        }
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isEmpty()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}



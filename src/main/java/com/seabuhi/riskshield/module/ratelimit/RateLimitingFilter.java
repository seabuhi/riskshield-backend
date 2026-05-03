package com.seabuhi.riskshield.module.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Senior Level: Distributed Rate Limiting using Redis.
 * Uses a Fixed Window counter strategy.
 * Works across all instances in a distributed environment.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

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

        String clientIp = getClientIp(request);
        String redisKey = "rate_limit:" + clientIp + ":" + path;
        
        long limit = getLimit(path);
        long windowSeconds = getWindow(path);

        // Atomic increment in Redis
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        
        if (currentCount != null && currentCount == 1) {
            // First request in the window, set expiration
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        if (currentCount != null && currentCount > limit) {
            log.warn("Rate limit EXCEEDED for IP={} path={}", clientIp, path);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            // Calculate remaining time for the window to reset
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ttl));
            
            response.getWriter().write(
                    "{\"success\":false,\"errorCode\":\"RATE_LIMIT_EXCEEDED\"," +
                    "\"message\":\"Çox sayda sorğu göndərdiniz. " +
                    (ttl > 0 ? ttl : windowSeconds) + " saniyə gözləyin.\"}"
            );
            return;
        }

        // Add headers for the client
        response.addHeader("X-Rate-Limit-Limit", String.valueOf(limit));
        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, limit - currentCount)));
        
        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/signup")
                || path.startsWith("/otp/")
                || path.startsWith("/auth/forgot-password");
    }

    private long getLimit(String path) {
        if (path.startsWith("/auth/login")) return 5;  // 5 attempts
        if (path.startsWith("/otp/"))       return 3;  // 3 OTPs
        return 10;                                     // 10 others
    }

    private long getWindow(String path) {
        if (path.startsWith("/otp/")) return 300; // 5 minutes (300s)
        return 60;                                // 1 minute (60s)
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isEmpty()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}


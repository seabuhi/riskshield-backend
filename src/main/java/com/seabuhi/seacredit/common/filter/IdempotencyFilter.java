package com.seabuhi.seacredit.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforces idempotency for POST/PUT/PATCH requests that carry an Idempotency-Key header.
 * In production, replace the ConcurrentHashMap with Redis for distributed environments.
 */
@Component
@Slf4j
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";

    // key → timestamp (for TTL cleanup in production use Redis with EXPIRE)
    private final Map<String, Long> processedKeys = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String method = request.getMethod();
        // Only enforce on mutating verbs
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(HEADER);
        if (key == null || key.isBlank()) {
            // No key provided → allow (header is optional)
            chain.doFilter(request, response);
            return;
        }

        // Check for duplicate
        if (processedKeys.containsKey(key)) {
            log.warn("Duplicate request detected — Idempotency-Key: {}", key);
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"success\":false,\"errorCode\":\"DUPLICATE_REQUEST\"," +
                    "\"message\":\"Bu əməliyyat artıq icra edilib (Idempotency-Key: " + key + ")\"}"
            );
            return;
        }

        processedKeys.put(key, System.currentTimeMillis());
        chain.doFilter(request, response);

        // Cleanup old keys (older than 24h) — in production use Redis TTL
        long cutoff = System.currentTimeMillis() - 86_400_000;
        processedKeys.entrySet().removeIf(e -> e.getValue() < cutoff);
    }
}



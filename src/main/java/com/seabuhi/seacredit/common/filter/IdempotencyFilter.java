package com.seabuhi.seacredit.common.filter;

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
 * Senior Level: Distributed Idempotency using Redis.
 * Prevents duplicate POST/PUT/PATCH requests across multiple instances.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";
    public static final String REDIS_PREFIX = "idempotency:";
    
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String method = request.getMethod();
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(HEADER);
        if (key == null || key.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        String redisKey = REDIS_PREFIX + key;

        // SETNX in Redis (Set if Not eXists) with TTL (10 minutes)
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(redisKey, "processed", 10, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(isAbsent)) {
            log.warn("Duplicate request detected in distributed environment — Idempotency-Key: {}", key);
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"success\":false,\"errorCode\":\"DUPLICATE_REQUEST\"," +
                    "\"message\":\"Bu əməliyyat artıq icra edilib (Idempotency-Key: " + key + ")\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}

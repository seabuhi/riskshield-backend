package com.seabuhi.seacredit.module.audit;

import com.seabuhi.seacredit.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String username = "anonymous";
        Long userId = null;

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            username = principal.getUsername();
            userId = principal.getId();
        }

        // Get request info
        String ipAddress = "unknown";
        String userAgent = "unknown";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ipAddress = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
            }
        } catch (Exception ignored) {}

        Object result = null;
        String outcome = "SUCCESS";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            outcome = "FAILURE";
            errorMessage = ex.getMessage();
            throw ex;
        } finally {
            auditService.log(username, userId, auditable.action(), auditable.resource(),
                    null, null, null, ipAddress, userAgent, outcome, errorMessage);
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}



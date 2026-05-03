package com.seabuhi.seacredit.module.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String username, Long userId, String action, String resource,
                    String resourceId, String beforeData, String afterData,
                    String ipAddress, String userAgent, String result, String errorMessage) {
        try {
            AuditLog entry = AuditLog.builder()
                    .username(username)
                    .userId(userId)
                    .action(action)
                    .resource(resource)
                    .resourceId(resourceId)
                    .beforeData(beforeData)
                    .afterData(afterData)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .result(result)
                    .errorMessage(errorMessage)
                    .build();

            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }
}



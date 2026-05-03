package com.seabuhi.riskshield.module.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryTask {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    @Transactional
    public void retryFailedNotifications() {
        log.info("Checking for failed notifications...");
        List<Notification> failedNotifications = notificationRepository.findByStatus("FAILED");

        for (Notification notification : failedNotifications) {
            if (notification.getRetryCount() < 3) {
                log.info("Retrying notification {} for {}", notification.getId(), notification.getRecipient());
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setStatus("RETRYING");
                
                // Perform actual send and update existing record
                emailService.sendActual(notification);
            } else {
                log.warn("Notification {} reached max retries", notification.getId());
                notification.setStatus("FAILED_PERMANENTLY");
                notificationRepository.save(notification);
            }
        }
    }
}


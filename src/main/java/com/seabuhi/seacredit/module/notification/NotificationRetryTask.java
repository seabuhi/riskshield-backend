package com.seabuhi.seacredit.module.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryTask {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    public void retryFailedNotifications() {
        log.info("Checking for failed notifications...");
        List<Notification> failedNotifications = notificationRepository.findByStatus("FAILED");

        for (Notification notification : failedNotifications) {
            if (notification.getRetryCount() < 3) {
                log.info("Retrying notification {} for {}", notification.getId(), notification.getRecipient());
                notification.setRetryCount(notification.getRetryCount() + 1);
                emailService.sendEmail(notification.getRecipient(), notification.getSubject(), notification.getContent());
                // The sendEmail method will update the status and retry count properly (actually it creates a new one, 
                // but let's just mark the old one as RETRIED)
                notification.setStatus("RETRIED");
                notificationRepository.save(notification);
            }
        }
    }
}



package com.seabuhi.seacredit.module.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    /**
     * Entry point for new email requests. Creates a record and sends asynchronously.
     */
    @Async
    @Transactional
    public void sendEmail(String to, String subject, String content) {
        Notification notification = Notification.builder()
                .recipient(to)
                .subject(subject)
                .content(content)
                .type("EMAIL")
                .status("PENDING")
                .retryCount(0)
                .build();
        
        notificationRepository.save(notification);
        sendActual(notification);
    }

    /**
     * Shared method to perform the actual sending and update the record.
     */
    @Transactional
    public void sendActual(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getContent());
            mailSender.send(message);

            notification.setStatus("SENT");
            notification.setErrorMessage(null);
        } catch (Exception e) {
            log.error("Failed to send email to {}", notification.getRecipient(), e);
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
        }
        notificationRepository.save(notification);
    }
}

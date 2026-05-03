package com.seabuhi.seacredit.module.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Async
    public void sendEmail(String to, String subject, String content) {
        Notification notification = Notification.builder()
                .recipient(to)
                .subject(subject)
                .content(content)
                .type("EMAIL")
                .status("PENDING")
                .build();
        
        notificationRepository.save(notification);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);

            notification.setStatus("SENT");
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
        }
        
        notificationRepository.save(notification);
    }
}



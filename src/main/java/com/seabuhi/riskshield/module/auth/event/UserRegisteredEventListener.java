package com.seabuhi.riskshield.module.auth.event;

import com.seabuhi.riskshield.module.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("UserRegisteredEvent received for userId={}, email={}", event.getUserId(), event.getEmail());

        // Send welcome email
        emailService.sendEmail(
                event.getEmail(),
                "RiskShield-ə xoş gəlmisiniz! 🎉",
                "Hörmətli " + event.getFullName() + ",\n\n" +
                "RiskShield platformasına qeydiyyatdan keçdiyiniz üçün təşəkkür edirik.\n" +
                "Emailinizi təsdiqləmək üçün OTP kodu göndərildi.\n\n" +
                "Hörmətlə,\nRiskShield Komandası"
        );
    }
}




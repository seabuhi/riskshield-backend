package com.seabuhi.seacredit.module.fraud;

import com.seabuhi.seacredit.config.FeatureToggle;
import com.seabuhi.seacredit.module.fraud.dto.FraudCheckRequest;
import com.seabuhi.seacredit.module.fraud.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final BlacklistRepository blacklistRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final FeatureToggle featureToggle;

    @Transactional
    public FraudCheckResponse analyzeTransaction(FraudCheckRequest request) {
        // Feature toggle check
        if (!featureToggle.isFraudDetection()) {
            log.info("Fraud detection is DISABLED via feature toggle");
            return FraudCheckResponse.builder()
                    .riskScore(0).severity("LOW").blocked(false)
                    .flags(Collections.emptyList()).build();
        }

        List<String> flags = new ArrayList<>();
        int riskScore = 0;

        // 1. Blacklist Check - IP
        if (request.getIpAddress() != null && blacklistRepository.existsByEntryValueAndTypeAndActiveTrue(request.getIpAddress(), "IP")) {
            flags.add("BLACKLISTED_IP");
            riskScore += 100;
        }

        // 2. Blacklist Check - Email
        if (request.getEmail() != null && blacklistRepository.existsByEntryValueAndTypeAndActiveTrue(request.getEmail(), "EMAIL")) {
            flags.add("BLACKLISTED_EMAIL");
            riskScore += 100;
        }

        // 3. High Transaction Amount
        if (request.getTransactionAmount() != null && request.getTransactionAmount() > 50000) {
            flags.add("HIGH_AMOUNT_TRANSACTION");
            riskScore += 40;
        }

        // 4. Suspicious hour (2AM - 5AM)
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 2 && hour <= 5) {
            flags.add("SUSPICIOUS_HOUR");
            riskScore += 20;
        }

        // 5. Unknown device
        if (request.getDeviceInfo() == null || request.getDeviceInfo().isBlank()) {
            flags.add("UNKNOWN_DEVICE");
            riskScore += 15;
        }

        // 6. Multiple rapid requests (needs Redis in production, simplified here)
        if (request.getRequestCount() != null && request.getRequestCount() > 10) {
            flags.add("RAPID_REQUESTS");
            riskScore += 30;
        }

        String severity;
        boolean blocked;
        if (riskScore >= 100) {
            severity = "CRITICAL";
            blocked = true;
        } else if (riskScore >= 60) {
            severity = "HIGH";
            blocked = true;
        } else if (riskScore >= 30) {
            severity = "MEDIUM";
            blocked = false;
        } else {
            severity = "LOW";
            blocked = false;
        }

        // Save alert if risk is detected
        if (!flags.isEmpty()) {
            FraudAlert alert = FraudAlert.builder()
                    .userId(request.getUserId())
                    .alertType(String.join(",", flags))
                    .severity(severity)
                    .details("Risk score: " + riskScore + " | Flags: " + String.join(", ", flags))
                    .ipAddress(request.getIpAddress())
                    .deviceInfo(request.getDeviceInfo())
                    .resolved(false)
                    .build();
            fraudAlertRepository.save(alert);
            log.warn("Fraud alert created: severity={}, flags={}", severity, flags);
        }

        return FraudCheckResponse.builder()
                .riskScore(riskScore)
                .severity(severity)
                .blocked(blocked)
                .flags(flags)
                .build();
    }

    @Transactional
    public void addToBlacklist(String entryValue, String type, String reason) {
        BlacklistEntry entry = BlacklistEntry.builder()
                .entryValue(entryValue)
                .type(type)
                .reason(reason)
                .active(true)
                .build();
        blacklistRepository.save(entry);
        log.info("Added to blacklist: {} ({})", entryValue, type);
    }

    @Transactional
    public void removeFromBlacklist(Long id) {
        blacklistRepository.findById(id).ifPresent(entry -> {
            entry.setActive(false);
            blacklistRepository.save(entry);
        });
    }

    public List<FraudAlert> getOpenAlerts() {
        return fraudAlertRepository.findByResolvedFalse();
    }

    @Transactional
    public void resolveAlert(Long alertId) {
        fraudAlertRepository.findById(alertId).ifPresent(alert -> {
            alert.setResolved(true);
            fraudAlertRepository.save(alert);
        });
    }

    public List<FraudAlert> getUserAlerts(Long userId) {
        return fraudAlertRepository.findByUserId(userId);
    }
}



package com.seabuhi.riskshield.module.fraud;

import com.seabuhi.riskshield.config.FeatureToggle;
import com.seabuhi.riskshield.module.fraud.dto.FraudCheckRequest;
import com.seabuhi.riskshield.module.fraud.dto.FraudCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock private BlacklistRepository blacklistRepository;
    @Mock private FraudAlertRepository fraudAlertRepository;
    @Mock private FeatureToggle featureToggle;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    void setUp() {
        lenient().when(featureToggle.isFraudDetection()).thenReturn(true);
    }

    @Test
    @DisplayName("Blacklisted IP → CRITICAL severity, blocked")
    void blacklistedIp_shouldBeCritical() {
        FraudCheckRequest req = new FraudCheckRequest();
        req.setIpAddress("1.2.3.4");
        req.setDeviceInfo("Chrome");

        when(blacklistRepository.existsByEntryValueAndTypeAndActiveTrue("1.2.3.4", "IP"))
                .thenReturn(true);

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertEquals("CRITICAL", res.getSeverity());
        assertTrue(res.isBlocked());
        assertTrue(res.getFlags().contains("BLACKLISTED_IP"));
        verify(fraudAlertRepository).save(any(FraudAlert.class));
    }

    @Test
    @DisplayName("Clean request → LOW severity, not blocked")
    void cleanRequest_shouldBeLow() {
        FraudCheckRequest req = new FraudCheckRequest();
        req.setIpAddress("10.0.0.1");
        req.setEmail("user@test.com");
        req.setDeviceInfo("Firefox");
        req.setTransactionAmount(500.0);

        when(blacklistRepository.existsByEntryValueAndTypeAndActiveTrue(anyString(), anyString()))
                .thenReturn(false);

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertEquals("LOW", res.getSeverity());
        assertFalse(res.isBlocked());
    }

    @Test
    @DisplayName("High transaction amount → flagged")
    void highAmount_shouldFlag() {
        FraudCheckRequest req = new FraudCheckRequest();
        req.setTransactionAmount(100000.0);
        req.setDeviceInfo("Chrome");

        when(blacklistRepository.existsByEntryValueAndTypeAndActiveTrue(any(), any()))
                .thenReturn(false);

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertTrue(res.getFlags().contains("HIGH_AMOUNT_TRANSACTION"));
        assertTrue(res.getRiskScore() >= 40);
    }

    @Test
    @DisplayName("Unknown device → adds UNKNOWN_DEVICE flag")
    void unknownDevice_shouldFlag() {
        FraudCheckRequest req = new FraudCheckRequest();
        req.setDeviceInfo("");

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertTrue(res.getFlags().contains("UNKNOWN_DEVICE"));
    }

    @Test
    @DisplayName("Rapid requests → adds RAPID_REQUESTS flag")
    void rapidRequests_shouldFlag() {
        FraudCheckRequest req = new FraudCheckRequest();
        req.setRequestCount(15);
        req.setDeviceInfo("Chrome");

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertTrue(res.getFlags().contains("RAPID_REQUESTS"));
    }

    @Test
    @DisplayName("Feature toggle OFF → returns safe LOW response")
    void featureDisabled_shouldReturnLow() {
        when(featureToggle.isFraudDetection()).thenReturn(false);

        FraudCheckRequest req = new FraudCheckRequest();
        req.setIpAddress("1.2.3.4");

        FraudCheckResponse res = fraudDetectionService.analyzeTransaction(req);

        assertEquals("LOW", res.getSeverity());
        assertFalse(res.isBlocked());
        assertEquals(0, res.getRiskScore());
        verifyNoInteractions(blacklistRepository);
    }
}




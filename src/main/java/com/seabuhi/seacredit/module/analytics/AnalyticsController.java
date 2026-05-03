package com.seabuhi.seacredit.module.analytics;

import com.seabuhi.seacredit.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /** Full dashboard snapshot */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        return ResponseEntity.ok(
                ApiResponse.ok(analyticsService.getDashboardSnapshot(), "Dashboard məlumatları"));
    }

    /** User statistics */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userStats() {
        return ResponseEntity.ok(
                ApiResponse.ok(analyticsService.getUserStats(), "İstifadəçi statistikası"));
    }

    /** Fraud statistics */
    @GetMapping("/fraud")
    public ResponseEntity<ApiResponse<Map<String, Object>>> fraudStats() {
        return ResponseEntity.ok(
                ApiResponse.ok(analyticsService.getFraudStats(), "Fırıldaqçılıq statistikası"));
    }

    /** Notification statistics */
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> notificationStats() {
        return ResponseEntity.ok(
                ApiResponse.ok(analyticsService.getNotificationStats(), "Bildiriş statistikası"));
    }
}



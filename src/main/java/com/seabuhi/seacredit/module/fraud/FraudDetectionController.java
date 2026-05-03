package com.seabuhi.seacredit.module.fraud;

import com.seabuhi.seacredit.common.response.ApiResponse;
import com.seabuhi.seacredit.module.fraud.dto.FraudCheckRequest;
import com.seabuhi.seacredit.module.fraud.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    /**
     * Analyze a transaction or login attempt for fraud signals.
     */
    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<FraudCheckResponse>> analyze(@RequestBody FraudCheckRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                fraudDetectionService.analyzeTransaction(request),
                "Analiz tamamlandı"
        ));
    }

    /**
     * Get all unresolved fraud alerts.
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getOpenAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(
                fraudDetectionService.getOpenAlerts(),
                "Açıq xəbərdarlıqlar"
        ));
    }

    /**
     * Get fraud alerts for a specific user.
     */
    @GetMapping("/alerts/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getUserAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(
                fraudDetectionService.getUserAlerts(userId),
                "İstifadəçi xəbərdarlıqları"
        ));
    }

    /**
     * Resolve (close) a fraud alert.
     */
    @PatchMapping("/alerts/{alertId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resolveAlert(@PathVariable Long alertId) {
        fraudDetectionService.resolveAlert(alertId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xəbərdarlıq həll edildi"));
    }

    /**
     * Add an IP or email to the blacklist.
     */
    @PostMapping("/blacklist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addToBlacklist(@RequestBody Map<String, String> request) {
        fraudDetectionService.addToBlacklist(
                request.get("value"),
                request.get("type"),
                request.get("reason")
        );
        return ResponseEntity.ok(ApiResponse.ok(null, "Qara siyahıya əlavə edildi"));
    }

    /**
     * Remove an entry from the blacklist.
     */
    @DeleteMapping("/blacklist/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeFromBlacklist(@PathVariable Long id) {
        fraudDetectionService.removeFromBlacklist(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Qara siyahıdan çıxarıldı"));
    }
}



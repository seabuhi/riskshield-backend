package com.seabuhi.riskshield.module.notification;

import com.seabuhi.riskshield.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService           emailService;
    private final NotificationRepository notificationRepository;
    private final NotificationRetryTask  retryTask;

    /** Admin: send a custom email */
    @PostMapping("/send-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@RequestBody Map<String, String> body) {
        emailService.sendEmail(body.get("to"), body.get("subject"), body.get("content"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Email göndərildi"));
    }

    /** Admin: full notification history (paginated) */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<Page<Notification>>> history(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Notification> result = notificationRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok(result, "Bildiriş tarixi"));
    }

    /** Admin: history for a specific email address */
    @GetMapping("/history/by-recipient")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Notification>>> historyByRecipient(
            @RequestParam String email) {

        return ResponseEntity.ok(
                ApiResponse.ok(notificationRepository.findByRecipient(email), "Alıcı bildiriş tarixi"));
    }

    /** Admin: list only failed notifications */
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Notification>>> failed() {
        return ResponseEntity.ok(
                ApiResponse.ok(notificationRepository.findByStatus("FAILED"), "Uğursuz bildirişlər"));
    }

    /** Admin: manually trigger retry of failed notifications */
    @PostMapping("/retry-failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> retryFailed() {
        retryTask.retryFailedNotifications();
        return ResponseEntity.ok(ApiResponse.ok(null, "Uğursuz bildirişlər yenidən göndərildi"));
    }
}




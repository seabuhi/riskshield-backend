package com.seabuhi.seacredit.module.audit;

import com.seabuhi.seacredit.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    /** Paginated list of all audit logs, newest first */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = auditLogRepository.findAll(
                PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return ResponseEntity.ok(ApiResponse.ok(logs, "Audit logları"));
    }

    /** Filter by username */
    @GetMapping("/by-user")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByUser(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = auditLogRepository.findByUsername(
                username, PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return ResponseEntity.ok(ApiResponse.ok(logs, "İstifadəçi audit logları"));
    }

    /** Filter by action type */
    @GetMapping("/by-action")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByAction(
            @RequestParam String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = auditLogRepository.findByAction(
                action, PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return ResponseEntity.ok(ApiResponse.ok(logs, "Əməliyyat audit logları"));
    }

    /** Filter by IP address */
    @GetMapping("/by-ip")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByIp(
            @RequestParam String ip,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = auditLogRepository.findByIpAddress(
                ip, PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return ResponseEntity.ok(ApiResponse.ok(logs, "IP audit logları"));
    }

    /** Filter by date range */
    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<AuditLog> logs = auditLogRepository.findByTimestampBetween(from, to);
        return ResponseEntity.ok(ApiResponse.ok(logs, "Tarix aralığı audit logları"));
    }

    /** Get a single log entry */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLog>> getById(@PathVariable Long id) {
        return auditLogRepository.findById(id)
                .map(log -> ResponseEntity.ok(ApiResponse.ok(log, "Audit log detalları")))
                .orElse(ResponseEntity.notFound().build());
    }
}



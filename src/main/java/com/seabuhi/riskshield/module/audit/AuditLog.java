package com.seabuhi.riskshield.module.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;        // Who did it
    private Long userId;

    @Column(nullable = false)
    private String action;          // What action (e.g. USER_LOGIN, LOAN_APPROVED)

    @Column(nullable = false)
    private String resource;        // What resource (e.g. User, LoanApplication)

    private String resourceId;      // ID of the affected record

    @Column(columnDefinition = "TEXT")
    private String beforeData;      // State before the change (JSON)

    @Column(columnDefinition = "TEXT")
    private String afterData;       // State after the change (JSON)

    private String ipAddress;
    private String userAgent;

    @Column(nullable = false)
    private String result;          // SUCCESS or FAILURE

    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}




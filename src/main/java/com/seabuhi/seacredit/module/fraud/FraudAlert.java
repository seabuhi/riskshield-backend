package com.seabuhi.seacredit.module.fraud;

import com.seabuhi.seacredit.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fraud_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String alertType; // SUSPICIOUS_LOGIN, HIGH_RISK_TRANSACTION, BLACKLISTED_IP

    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;
    private String deviceInfo;

    @Builder.Default
    private boolean resolved = false;
}



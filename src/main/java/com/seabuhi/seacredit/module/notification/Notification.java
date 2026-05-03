package com.seabuhi.seacredit.module.notification;

import com.seabuhi.seacredit.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String type; // e.g., EMAIL, SMS

    @Builder.Default
    private String status = "PENDING"; // PENDING, SENT, FAILED

    private int retryCount;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}



package com.seabuhi.riskshield.module.auth;

import com.seabuhi.riskshield.module.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_otps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String purpose; // e.g., SIGNUP_VERIFICATION, PASSWORD_RESET

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean used = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}




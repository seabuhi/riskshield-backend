package com.seabuhi.riskshield.module.fraud;

import com.seabuhi.riskshield.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blacklist_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_value", nullable = false)
    private String entryValue; // IP address or email

    @Column(nullable = false)
    private String type; // IP or EMAIL

    private String reason;

    @Builder.Default
    private boolean active = true;
}




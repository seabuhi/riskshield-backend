package com.seabuhi.riskshield.module.document;

import com.seabuhi.riskshield.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String documentType; // PASSPORT, INCOME_PROOF, ADDRESS_PROOF, BANK_STATEMENT

    @Column(nullable = false)
    private String contentType;

    private Long fileSize;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
}




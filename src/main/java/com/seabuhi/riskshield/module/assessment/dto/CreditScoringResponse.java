package com.seabuhi.riskshield.module.assessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditScoringResponse {
    private int score;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private boolean eligible;
    private double maxLoanAmount;
    private double monthlyPaymentCapacity;
}




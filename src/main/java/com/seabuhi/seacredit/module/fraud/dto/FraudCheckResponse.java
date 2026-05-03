package com.seabuhi.seacredit.module.fraud.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FraudCheckResponse {
    private int riskScore;
    private String severity;   // LOW, MEDIUM, HIGH, CRITICAL
    private boolean blocked;
    private List<String> flags;
}



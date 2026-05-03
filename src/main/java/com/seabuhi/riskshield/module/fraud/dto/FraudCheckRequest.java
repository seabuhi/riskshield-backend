package com.seabuhi.riskshield.module.fraud.dto;

import lombok.Data;

@Data
public class FraudCheckRequest {
    private Long userId;
    private String ipAddress;
    private String email;
    private String deviceInfo;
    private Double transactionAmount;
    private Integer requestCount;
}




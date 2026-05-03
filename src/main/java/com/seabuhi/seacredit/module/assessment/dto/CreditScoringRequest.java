package com.seabuhi.seacredit.module.assessment.dto;

import lombok.Data;

@Data
public class CreditScoringRequest {
    private double monthlyIncome;
    private double monthlyExpenses;
    private int employmentYears;
    private int age;
    private boolean hasExistingLoans;
}



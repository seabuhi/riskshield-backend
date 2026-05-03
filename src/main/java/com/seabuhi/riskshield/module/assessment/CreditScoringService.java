package com.seabuhi.riskshield.module.assessment;

import com.seabuhi.riskshield.module.assessment.dto.CreditScoringRequest;
import com.seabuhi.riskshield.module.assessment.dto.CreditScoringResponse;
import org.springframework.stereotype.Service;

@Service
public class CreditScoringService {

    public CreditScoringResponse calculateScore(CreditScoringRequest request) {
        int score = 0;

        // 1. Income Factor (Max 40 points)
        double netIncome = request.getMonthlyIncome() - request.getMonthlyExpenses();
        if (netIncome > 5000) score += 40;
        else if (netIncome > 3000) score += 30;
        else if (netIncome > 1000) score += 20;
        else if (netIncome > 500) score += 10;

        // 2. Employment Factor (Max 30 points)
        if (request.getEmploymentYears() > 10) score += 30;
        else if (request.getEmploymentYears() > 5) score += 20;
        else if (request.getEmploymentYears() > 2) score += 10;

        // 3. Age Factor (Max 20 points)
        if (request.getAge() >= 25 && request.getAge() <= 50) score += 20;
        else if (request.getAge() > 50 && request.getAge() <= 65) score += 10;

        // 4. Debt Factor (Penalty)
        if (request.isHasExistingLoans()) score -= 20;

        // Risk Level
        String riskLevel;
        boolean eligible;
        if (score >= 70) {
            riskLevel = "LOW";
            eligible = true;
        } else if (score >= 40) {
            riskLevel = "MEDIUM";
            eligible = true;
        } else {
            riskLevel = "HIGH";
            eligible = false;
        }

        double capacity = Math.max(0, netIncome * 0.4); // 40% of net income
        double maxLoan = eligible ? capacity * 36 : 0; // 3 year loan estimate

        return CreditScoringResponse.builder()
                .score(Math.max(0, score))
                .riskLevel(riskLevel)
                .eligible(eligible)
                .monthlyPaymentCapacity(capacity)
                .maxLoanAmount(maxLoan)
                .build();
    }
}




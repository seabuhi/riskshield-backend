package com.seabuhi.seacredit.module.assessment;

import com.seabuhi.seacredit.module.assessment.dto.CreditScoringRequest;
import com.seabuhi.seacredit.module.assessment.dto.CreditScoringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreditScoringServiceTest {

    private CreditScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new CreditScoringService();
    }

    @Test
    @DisplayName("High income + long employment = LOW risk, eligible")
    void highIncome_longEmployment_shouldBeLowRisk() {
        CreditScoringRequest req = new CreditScoringRequest();
        req.setMonthlyIncome(10000);
        req.setMonthlyExpenses(3000);
        req.setEmploymentYears(12);
        req.setAge(35);
        req.setHasExistingLoans(false);

        CreditScoringResponse res = scoringService.calculateScore(req);

        assertEquals("LOW", res.getRiskLevel());
        assertTrue(res.isEligible());
        assertTrue(res.getScore() >= 70);
        assertTrue(res.getMaxLoanAmount() > 0);
    }

    @Test
    @DisplayName("Low income + no employment = HIGH risk, not eligible")
    void lowIncome_noEmployment_shouldBeHighRisk() {
        CreditScoringRequest req = new CreditScoringRequest();
        req.setMonthlyIncome(400);
        req.setMonthlyExpenses(350);
        req.setEmploymentYears(0);
        req.setAge(19);
        req.setHasExistingLoans(true);

        CreditScoringResponse res = scoringService.calculateScore(req);

        assertEquals("HIGH", res.getRiskLevel());
        assertFalse(res.isEligible());
        assertEquals(0, res.getMaxLoanAmount());
    }

    @Test
    @DisplayName("Medium income = MEDIUM risk")
    void mediumIncome_shouldBeMediumRisk() {
        CreditScoringRequest req = new CreditScoringRequest();
        req.setMonthlyIncome(4000);
        req.setMonthlyExpenses(2500);
        req.setEmploymentYears(3);
        req.setAge(30);
        req.setHasExistingLoans(false);

        CreditScoringResponse res = scoringService.calculateScore(req);

        assertEquals("MEDIUM", res.getRiskLevel());
        assertTrue(res.isEligible());
    }

    @Test
    @DisplayName("Monthly payment capacity = 40% of net income")
    void paymentCapacity_shouldBe40PercentOfNet() {
        CreditScoringRequest req = new CreditScoringRequest();
        req.setMonthlyIncome(5000);
        req.setMonthlyExpenses(2000);
        req.setEmploymentYears(6);
        req.setAge(40);
        req.setHasExistingLoans(false);

        CreditScoringResponse res = scoringService.calculateScore(req);

        double expectedCapacity = (5000 - 2000) * 0.4;
        assertEquals(expectedCapacity, res.getMonthlyPaymentCapacity(), 0.01);
    }

    @Test
    @DisplayName("Existing loans penalty reduces score by 20")
    void existingLoans_shouldReduceScore() {
        CreditScoringRequest withoutLoans = new CreditScoringRequest();
        withoutLoans.setMonthlyIncome(6000);
        withoutLoans.setMonthlyExpenses(2000);
        withoutLoans.setEmploymentYears(6);
        withoutLoans.setAge(35);
        withoutLoans.setHasExistingLoans(false);

        CreditScoringRequest withLoans = new CreditScoringRequest();
        withLoans.setMonthlyIncome(6000);
        withLoans.setMonthlyExpenses(2000);
        withLoans.setEmploymentYears(6);
        withLoans.setAge(35);
        withLoans.setHasExistingLoans(true);

        int scoreWithout = scoringService.calculateScore(withoutLoans).getScore();
        int scoreWith    = scoringService.calculateScore(withLoans).getScore();

        assertEquals(20, scoreWithout - scoreWith);
    }
}



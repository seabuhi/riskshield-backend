package com.seabuhi.riskshield.module.fraud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByUserId(Long userId);
    List<FraudAlert> findByResolvedFalse();
    long countByResolvedFalse();
    List<FraudAlert> findBySeverity(String severity);
}




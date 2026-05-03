package com.seabuhi.riskshield.module.fraud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistEntry, Long> {
    Optional<BlacklistEntry> findByEntryValueAndTypeAndActiveTrue(String entryValue, String type);
    boolean existsByEntryValueAndTypeAndActiveTrue(String entryValue, String type);
}




package com.seabuhi.riskshield.module.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {
    List<CustomerDocument> findByUserIdAndDeletedFalse(Long userId);
    List<CustomerDocument> findByUserIdAndDocumentTypeAndDeletedFalse(Long userId, String documentType);
    Optional<CustomerDocument> findByIdAndDeletedFalse(Long id);
}


package com.seabuhi.seacredit.module.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {
    List<CustomerDocument> findByUserId(Long userId);
    List<CustomerDocument> findByUserIdAndDocumentType(Long userId, String documentType);
}



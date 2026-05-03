package com.seabuhi.seacredit.module.document;

import com.seabuhi.seacredit.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final CustomerDocumentRepository documentRepository;

    private static final Set<String> ALLOWED_DOC_TYPES = Set.of(
            "PASSPORT", "INCOME_PROOF", "ADDRESS_PROOF",
            "BANK_STATEMENT", "TAX_CERTIFICATE", "EMPLOYMENT_LETTER"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/jpg"
    );

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadDir;

    // ─── Upload ─────────────────────────────────────────────────────────────────

    @Transactional
    public CustomerDocument upload(Long userId, MultipartFile file, String documentType) {

        if (!ALLOWED_DOC_TYPES.contains(documentType.toUpperCase())) {
            throw new BusinessException("INVALID_DOCUMENT_TYPE",
                    "Yanlış sənəd növü. İcazə verilənlər: " + ALLOWED_DOC_TYPES);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException("INVALID_FILE_TYPE",
                    "Yalnız PDF, JPG, PNG faylları qəbul edilir");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BusinessException("FILE_TOO_LARGE",
                    "Fayl həcmi 10 MB-dan çox ola bilməz");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String extension     = getExtension(file.getOriginalFilename());
            String storedName    = UUID.randomUUID() + extension;
            Path   targetPath    = uploadPath.resolve(storedName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            CustomerDocument doc = CustomerDocument.builder()
                    .userId(userId)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedName)
                    .filePath(targetPath.toString())
                    .documentType(documentType.toUpperCase())
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .status("PENDING")
                    .build();

            return documentRepository.save(doc);

        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException("FILE_UPLOAD_FAILED", "Fayl yüklənə bilmədi");
        }
    }

    // ─── Download ────────────────────────────────────────────────────────────────

    public Resource download(Long documentId, Long userId) {
        CustomerDocument doc = findSecure(documentId, userId);
        try {
            Path   filePath = Paths.get(doc.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new BusinessException("FILE_NOT_FOUND", "Fayl tapılmadı");
        } catch (MalformedURLException e) {
            throw new BusinessException("FILE_NOT_FOUND", "Fayl tapılmadı");
        }
    }

    // ─── Delete (Soft) ───────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long documentId, Long userId) {
        CustomerDocument doc = findSecure(documentId, userId);
        doc.softDelete();
        documentRepository.save(doc);
    }

    // ─── Query ───────────────────────────────────────────────────────────────────

    public List<CustomerDocument> getMyDocuments(Long userId) {
        return documentRepository.findByUserIdAndDeletedFalse(userId);
    }

    public CustomerDocument getById(Long id) {
        return documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException("DOC_NOT_FOUND", "Sənəd tapılmadı"));
    }

    // ─── Admin: update status ─────────────────────────────────────────────────────

    @Transactional
    public void updateStatus(Long documentId, String status) {
        CustomerDocument doc = documentRepository.findByIdAndDeletedFalse(documentId)
                .orElseThrow(() -> new BusinessException("DOC_NOT_FOUND", "Sənəd tapılmadı"));
        doc.setStatus(status.toUpperCase());
        documentRepository.save(doc);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private CustomerDocument findSecure(Long documentId, Long userId) {
        CustomerDocument doc = documentRepository.findByIdAndDeletedFalse(documentId)
                .orElseThrow(() -> new BusinessException("DOC_NOT_FOUND", "Sənəd tapılmadı"));
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "Bu sənədə girişiniz yoxdur");
        }
        return doc;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        return (idx >= 0) ? filename.substring(idx) : "";
    }
}

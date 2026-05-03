package com.seabuhi.riskshield.module.document;

import com.seabuhi.riskshield.common.response.ApiResponse;
import com.seabuhi.riskshield.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /** Upload a new document */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CustomerDocument>> upload(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {

        CustomerDocument doc = documentService.upload(principal.getId(), file, documentType);
        return ResponseEntity.ok(ApiResponse.ok(doc, "Sənəd uğurla yükləndi"));
    }

    /** Download a document by its ID */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        Resource resource = documentService.download(id, principal.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /** Delete a document */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        documentService.delete(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Sənəd silindi"));
    }

    /** List current user's documents */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CustomerDocument>>> myDocuments(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                ApiResponse.ok(documentService.getMyDocuments(principal.getId()), "Sənədlərim"));
    }

    /** Admin: update document status (APPROVED / REJECTED) */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        documentService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Status yeniləndi"));
    }

    /** Allowed document types info */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> allowedTypes() {
        return ResponseEntity.ok(ApiResponse.ok(
                List.of("PASSPORT", "INCOME_PROOF", "ADDRESS_PROOF",
                        "BANK_STATEMENT", "TAX_CERTIFICATE", "EMPLOYMENT_LETTER"),
                "İcazə verilən sənəd növləri"
        ));
    }
}




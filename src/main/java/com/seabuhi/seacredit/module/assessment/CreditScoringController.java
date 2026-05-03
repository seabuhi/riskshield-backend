package com.seabuhi.seacredit.module.assessment;

import com.seabuhi.seacredit.common.response.ApiResponse;
import com.seabuhi.seacredit.module.assessment.dto.CreditScoringRequest;
import com.seabuhi.seacredit.module.assessment.dto.CreditScoringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit-scoring")
@RequiredArgsConstructor
public class CreditScoringController {

    private final CreditScoringService creditScoringService;

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'CLIENT')")
    public ResponseEntity<ApiResponse<CreditScoringResponse>> calculate(@RequestBody CreditScoringRequest request) {
        CreditScoringResponse result = creditScoringService.calculateScore(request);
        return ResponseEntity.ok(ApiResponse.ok(result, "Kredit skoru hesablandı"));
    }

    @GetMapping("/risk-levels")
    public ResponseEntity<ApiResponse<Object>> getRiskLevels() {
        return ResponseEntity.ok(ApiResponse.ok(
                new Object[]{
                        java.util.Map.of("level", "LOW", "scoreRange", "70-100", "description", "Aşağı risk - Kredit verilir"),
                        java.util.Map.of("level", "MEDIUM", "scoreRange", "40-69", "description", "Orta risk - Şərtli kredit verilir"),
                        java.util.Map.of("level", "HIGH", "scoreRange", "0-39", "description", "Yüksək risk - Kredit rədd edilir")
                },
                "Risk səviyyələri"
        ));
    }
}



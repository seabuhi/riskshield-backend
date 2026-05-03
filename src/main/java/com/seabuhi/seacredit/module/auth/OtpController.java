package com.seabuhi.seacredit.module.auth;

import com.seabuhi.seacredit.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody Map<String, String> request) {
        otpService.sendOtp(request.get("email"), request.get("purpose"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Təsdiqləmə kodu göndərildi"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody Map<String, String> request) {
        otpService.verifyOtp(request.get("email"), request.get("code"), request.get("purpose"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Təsdiqləmə uğurludur"));
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody Map<String, String> request) {
        otpService.resendOtp(request.get("email"), request.get("purpose"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Təsdiqləmə kodu yenidən göndərildi"));
    }

    @GetMapping("/expire-time")
    public ResponseEntity<ApiResponse<LocalDateTime>> getExpireTime(@RequestParam String email, @RequestParam String purpose) {
        return ResponseEntity.ok(ApiResponse.ok(otpService.getOtpExpireTime(email, purpose), "OTP vaxtı"));
    }
}



package com.seabuhi.seacredit.module.auth;

import com.seabuhi.seacredit.common.response.ApiResponse;
import com.seabuhi.seacredit.module.auth.dto.LoginRequest;
import com.seabuhi.seacredit.module.auth.dto.LoginResponse;
import com.seabuhi.seacredit.module.auth.dto.SignupRequest;
import com.seabuhi.seacredit.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request), "Giriş uğurludur"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<com.seabuhi.seacredit.module.auth.dto.TokenRefreshResponse>> refresh(@Valid @RequestBody com.seabuhi.seacredit.module.auth.dto.TokenRefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request), "Token yeniləndi"));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Qeydiyyat uğurludur. Emailinizi təsdiqləyin."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.ok(null, "Çıxış uğurludur"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPrincipal>> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(principal, "İstifadəçi məlumatları"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody Map<String, String> request) {
        authService.changePassword(request.get("currentPassword"), request.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Şifrə uğurla dəyişdirildi"));
    }
}




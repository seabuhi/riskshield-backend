package com.seabuhi.riskshield.module.auth;

import com.seabuhi.riskshield.common.exception.BusinessException;
import com.seabuhi.riskshield.module.auth.dto.*;
import com.seabuhi.riskshield.module.user.*;
import com.seabuhi.riskshield.security.JwtTokenProvider;
import com.seabuhi.riskshield.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByIdAndDeletedFalse(userPrincipal.getId())
                .orElseThrow(() -> new BusinessException("İstifadəçi tapılmadı"));

        if (!user.isVerified()) {
            throw new BusinessException("NOT_VERIFIED", "Zəhmət olmasa email ünvanınızı təsdiqləyin");
        }

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenStr = tokenProvider.generateRefreshToken(userPrincipal.getUsername());

        saveRefreshToken(user, refreshTokenStr);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .fullName(userPrincipal.getFullName())
                .roles(roles)
                .build();
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsernameAndDeletedFalse(request.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "İstifadəçi adı artıq tutulub");
        }
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Bu email artıq qeydiyyatdan keçib");
        }

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseGet(() -> roleRepository.save(Role.builder().name("CLIENT").build()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .active(true)
                .verified(false)
                .roles(Set.of(clientRole))
                .build();

        userRepository.save(user);

        // Generate OTP via OtpService
        otpService.generateAndSaveOtp(user, "SIGNUP_VERIFICATION");

        // Publish event
        eventPublisher.publishEvent(
                new com.seabuhi.riskshield.module.auth.event.UserRegisteredEvent(this, user.getId(), user.getEmail(), user.getFullName())
        );
    }

    @Transactional
    public void logout() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByIdAndDeletedFalse(userPrincipal.getId()).orElseThrow();
        refreshTokenRepository.deleteByUser(user);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByIdAndDeletedFalse(userPrincipal.getId()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("INVALID_PASSWORD", "Cari şifrə yanlışdır");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        // 1. Get username from token
        String username = tokenProvider.getUsernameFromRefreshToken(requestRefreshToken);
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));

        // 2. Find hashed token record for this user
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("REFRESH_TOKEN_NOT_FOUND", "Refresh token tapılmadı"));

        // 3. Verify security
        if (refreshToken.isRevoked()) {
            throw new BusinessException("TOKEN_REVOKED", "Bu token ləğv edilib");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("TOKEN_EXPIRED", "Refresh token müddəti bitib");
        }
        
        // 4. Verify hash
        if (!passwordEncoder.matches(requestRefreshToken, refreshToken.getToken())) {
            throw new BusinessException("INVALID_TOKEN", "Refresh token yanlışdır");
        }

        // 5. Generate new access token
        String newAccessToken = tokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(UserPrincipal.create(user), null, 
                        user.getRoles().stream().map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toList()))
        );

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestRefreshToken)
                .build();
    }

    private void saveRefreshToken(User user, String token) {
        refreshTokenRepository.deleteByUser(user);

        // Save hashed token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(passwordEncoder.encode(token))
                .expiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1000000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}


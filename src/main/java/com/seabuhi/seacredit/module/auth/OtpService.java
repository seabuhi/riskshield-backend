package com.seabuhi.seacredit.module.auth;

import com.seabuhi.seacredit.common.exception.BusinessException;
import com.seabuhi.seacredit.module.user.User;
import com.seabuhi.seacredit.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserOtpRepository userOtpRepository;
    private final UserRepository userRepository;
    private final com.seabuhi.seacredit.module.notification.EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void sendOtp(String email, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));
        
        generateAndSaveOtp(user, purpose);
    }

    @Transactional
    public void verifyOtp(String email, String code, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));

        UserOtp otp = userOtpRepository.findTopByUserAndPurposeAndUsedOrderByCreatedAtDesc(user, purpose, false)
                .orElseThrow(() -> new BusinessException("OTP_NOT_FOUND", "Təsdiqləmə kodu tapılmadı"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP_EXPIRED", "Təsdiqləmə kodunun vaxtı bitib");
        }

        if (!otp.getCode().equals(code)) {
            throw new BusinessException("INVALID_OTP", "Təsdiqləmə kodu yanlışdır");
        }

        otp.setUsed(true);
        userOtpRepository.save(otp);
        
        if ("SIGNUP_VERIFICATION".equals(purpose)) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    @Transactional
    public void resendOtp(String email, String purpose) {
        // Implement logic to prevent spam (e.g., check last OTP time)
        sendOtp(email, purpose);
    }

    public LocalDateTime getOtpExpireTime(String email, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));

        return userOtpRepository.findTopByUserAndPurposeAndUsedOrderByCreatedAtDesc(user, purpose, false)
                .map(UserOtp::getExpiresAt)
                .orElse(null);
    }

    private void generateAndSaveOtp(User user, String purpose) {
        String code = String.format("%06d", random.nextInt(1000000));
        UserOtp otp = UserOtp.builder()
                .user(user)
                .code(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        userOtpRepository.save(otp);
        emailService.sendEmail(user.getEmail(), "Sea-Credit Təsdiqləmə Kodu", 
                "Sizin təsdiqləmə kodunuz: " + code + "\nMəqsəd: " + purpose);
    }
}




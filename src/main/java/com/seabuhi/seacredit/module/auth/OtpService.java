package com.seabuhi.seacredit.module.auth;

import com.seabuhi.seacredit.common.exception.BusinessException;
import com.seabuhi.seacredit.module.user.User;
import com.seabuhi.seacredit.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void sendOtp(String email, String purpose) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));
        
        generateAndSaveOtp(user, purpose);
    }

    @Transactional
    public void verifyOtp(String email, String code, String purpose) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));

        UserOtp otp = userOtpRepository.findTopByUserAndPurposeAndUsedOrderByCreatedAtDesc(user, purpose, false)
                .orElseThrow(() -> new BusinessException("OTP_NOT_FOUND", "Təsdiqləmə kodu tapılmadı"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP_EXPIRED", "Təsdiqləmə kodunun vaxtı bitib");
        }

        // Verify hashed OTP
        if (!passwordEncoder.matches(code, otp.getCode())) {
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
        sendOtp(email, purpose);
    }

    public LocalDateTime getOtpExpireTime(String email, String purpose) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "İstifadəçi tapılmadı"));

        return userOtpRepository.findTopByUserAndPurposeAndUsedOrderByCreatedAtDesc(user, purpose, false)
                .map(UserOtp::getExpiresAt)
                .orElse(null);
    }

    public void generateAndSaveOtp(User user, String purpose) {
        String plainCode = String.format("%06d", random.nextInt(1000000));
        
        // Save hashed code for security
        UserOtp otp = UserOtp.builder()
                .user(user)
                .code(passwordEncoder.encode(plainCode))
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        userOtpRepository.save(otp);
        
        emailService.sendEmail(user.getEmail(), "Sea-Credit Təsdiqləmə Kodu", 
                "Sizin təsdiqləmə kodunuz: " + plainCode + "\nMəqsəd: " + purpose);
    }
}

package com.seabuhi.seacredit.module.auth.dto;

import com.seabuhi.seacredit.common.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "İstifadəçi adı boş ola bilməz")
    @Size(min = 3, max = 50, message = "İstifadəçi adı 3-50 simvol arasında olmalıdır")
    private String username;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Düzgün email daxil edin")
    private String email;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @ValidPassword
    private String password;

    @NotBlank(message = "Ad soyad boş ola bilməz")
    @Size(min = 2, max = 100)
    private String fullName;

    private String phone;
}



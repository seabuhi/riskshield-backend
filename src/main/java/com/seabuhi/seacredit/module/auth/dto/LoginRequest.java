package com.seabuhi.seacredit.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "İstifadəçi adı və ya email boş ola bilməz")
    private String username;

    @NotBlank(message = "Şifrə boş ola bilməz")
    private String password;
}



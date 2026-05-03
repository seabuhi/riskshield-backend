package com.seabuhi.riskshield.module.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String fullName;
    private List<String> roles;
}




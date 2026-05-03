package com.seabuhi.riskshield.module.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private boolean active;
    private boolean verified;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




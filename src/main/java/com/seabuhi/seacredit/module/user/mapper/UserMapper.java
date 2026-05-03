package com.seabuhi.seacredit.module.user.mapper;

import com.seabuhi.seacredit.module.user.Role;
import com.seabuhi.seacredit.module.user.User;
import com.seabuhi.seacredit.module.user.dto.UserDto;

import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {}

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .active(user.isActive())
                .verified(user.isVerified())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}



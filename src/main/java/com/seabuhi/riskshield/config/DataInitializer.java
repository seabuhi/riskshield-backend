package com.seabuhi.riskshield.config;

import com.seabuhi.riskshield.module.user.Role;
import com.seabuhi.riskshield.module.user.RoleRepository;
import com.seabuhi.riskshield.module.user.User;
import com.seabuhi.riskshield.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.admin.username}")
    private String adminUsername;

    @org.springframework.beans.factory.annotation.Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        List<String> roles = List.of("ADMIN", "ANALYST", "CLIENT");
        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Rol yaradıldı: {}", roleName);
            }
        }
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsernameAndDeletedFalse(adminUsername)) return;

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        User admin = User.builder()
                .username(adminUsername)
                .email("admin@riskshield.az")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .fullName("System Administrator")
                .phone("+994501234567")
                .active(true)
                .verified(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.info("Default admin istifadəçisi yaradıldı: {}", adminUsername);
    }
}




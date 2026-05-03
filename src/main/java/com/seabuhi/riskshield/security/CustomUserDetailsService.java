package com.seabuhi.riskshield.security;

import com.seabuhi.riskshield.module.user.User;
import com.seabuhi.riskshield.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmailAndDeletedFalse(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı: " + usernameOrEmail));

        return UserPrincipal.create(user);
    }
}




package com.seabuhi.seacredit.module.admin;

import com.seabuhi.seacredit.common.exception.ResourceNotFoundException;
import com.seabuhi.seacredit.module.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Page<User> listUsers(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public void blockUser(Long id) {
        User user = getUser(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = getUser(id);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void changeRole(Long userId, String roleName) {
        User user = getUser(userId);
        Role role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }

    /** Soft delete — marks as deleted instead of hard-removing from DB */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        user.softDelete();
        user.setActive(false);
        userRepository.save(user);
    }
}



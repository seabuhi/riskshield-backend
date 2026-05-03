package com.seabuhi.seacredit.module.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    Optional<User> findByEmailAndDeletedFalse(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsernameAndDeletedFalse(String username);
    Boolean existsByEmailAndDeletedFalse(String email);

    Page<User> findByDeletedFalse(Pageable pageable);

    Page<User> findByDeletedFalseAndUsernameContainingIgnoreCaseOrDeletedFalseAndEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);

    long countByActiveTrueAndDeletedFalse();
    long countByVerifiedFalseAndDeletedFalse();
    long countByDeletedFalse();
}

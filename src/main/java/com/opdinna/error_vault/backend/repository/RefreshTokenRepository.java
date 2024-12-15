package com.opdinna.error_vault.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdinna.error_vault.backend.model.domain.RefreshToken;
import com.opdinna.error_vault.backend.model.domain.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}

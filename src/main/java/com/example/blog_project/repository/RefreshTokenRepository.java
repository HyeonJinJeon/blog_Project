package com.example.blog_project.repository;

import com.example.blog_project.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value);
    boolean existsByValue(String token);

    Optional<RefreshToken> findByUserId(Long userId);
}

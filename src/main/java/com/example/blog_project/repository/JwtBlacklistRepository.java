package com.example.blog_project.repository;

import com.example.blog_project.domain.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {
    boolean existsByToken(String token);
}

package com.example.blog_project.service;

import com.example.blog_project.domain.JwtBlacklist;
import com.example.blog_project.repository.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtBlackListService {
    private final JwtBlacklistRepository jwtBlacklistRepository;
    public void save(JwtBlacklist blacklist) {
        jwtBlacklistRepository.save(blacklist);
    }
}

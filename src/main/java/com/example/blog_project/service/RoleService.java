package com.example.blog_project.service;

import com.example.blog_project.domain.Role;
import com.example.blog_project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role getRole(Long id) {
        return roleRepository.findById(Math.toIntExact(id)).orElse(null); // 기본 값으로 null 반환
    }
}

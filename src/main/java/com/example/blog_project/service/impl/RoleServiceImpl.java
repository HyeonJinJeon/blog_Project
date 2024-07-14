package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Role;
import com.example.blog_project.repository.RoleRepository;
import com.example.blog_project.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    //사용자의 role을 가져오기 위해 작성
    //1. 일반사용자로 회원가입 할 수 있도록 role을 가져올 때 사용
    //2. 로그인을 했을 떄 관리자인지 판별할 때 사용
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElse(null); // 기본 값으로 null 반환
    }
}

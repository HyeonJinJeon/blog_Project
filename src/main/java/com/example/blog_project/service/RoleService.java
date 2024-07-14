package com.example.blog_project.service;

import com.example.blog_project.domain.Role;

public interface RoleService {
    //사용자의 role을 가져오기 위해 작성
    //1. 일반사용자로 회원가입 할 수 있도록 role을 가져올 때 사용
    //2. 로그인을 했을 떄 관리자인지 판별할 때 사용
    Role getRole(Long id);
}

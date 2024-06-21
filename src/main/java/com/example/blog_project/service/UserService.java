package com.example.blog_project.service;

import com.example.blog_project.domain.User;
import com.example.blog_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean authenticateByEmail(String username, String password) {
        // 이메일을 기준으로 사용자 조회
        User user = userRepository.findByUsername(username);

        // 사용자가 존재하고 비밀번호가 일치하는지 확인
        return user != null && password.equals(user.getPassword());
    }
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("이미 등록된 userName입니다.");
        }
        // 데이터베이스에 유저 정보 저장
        return userRepository.save(user);
    }
}

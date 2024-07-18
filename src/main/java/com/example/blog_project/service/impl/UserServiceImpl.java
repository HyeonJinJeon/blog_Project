package com.example.blog_project.service.impl;

import com.example.blog_project.domain.User;
import com.example.blog_project.repository.UserRepository;
import com.example.blog_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        //비밀번호 암호화해서 넣어주기
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 데이터베이스에 유저 정보 저장
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // 기존 사용자 정보 조회
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        System.out.println(existingUser.getUsername());
        if (existingUser != null) {
            System.out.println("사용자 있냐");
            // 수정할 필드 업데이트
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setProfileImageUrl(user.getProfileImageUrl());

            // 저장
            return userRepository.save(existingUser);
        }

        return null; // 사용자가 존재하지 않을 경우 예외 처리 필요
    }

    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
}

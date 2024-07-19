package com.example.blog_project.service;

import com.example.blog_project.domain.User;

import java.util.Optional;

public interface UserService {

    User findUserByUsername(String username);

    boolean authenticateByEmail(String username, String password);

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUser(Long id);

    boolean isUsernameDuplicate(String username);
}

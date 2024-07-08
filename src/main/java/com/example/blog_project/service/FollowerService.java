package com.example.blog_project.service;

import com.example.blog_project.repository.FollowerRepository;
import com.example.blog_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.blog_project.domain.User;
import com.example.blog_project.domain.Follower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository; // User 엔티티를 위한 리포지토리

    public void followUser(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Invalid follower ID"));

        if (!followerRepository.existsByUserAndFollower(user, follower)) {
            Follower newFollower = new Follower(user, follower);
            followerRepository.save(newFollower);
        }
    }

    public void unfollowUser(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Invalid follower ID"));

        if (followerRepository.existsByUserAndFollower(user, follower)) {
            followerRepository.deleteByUserAndFollower(user, follower);
        }
    }

    public boolean isFollowing(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Invalid follower ID"));
        return followerRepository.existsByUserAndFollower(user, follower);
    }

    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        return followerRepository.countByUser(user);
    }

    public long getFollowingCount(Long userId) {
        User follower = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        return followerRepository.countByFollower(follower);
    }
}

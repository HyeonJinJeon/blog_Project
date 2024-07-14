package com.example.blog_project.service;

import org.springframework.transaction.annotation.Transactional;

public interface FollowService {
    @Transactional
    long getFollowerCount(Long blogUserId);

    @Transactional
    long getFollowingCount(Long userId);

    @Transactional
    boolean isFollowing(Long blogUserId, Long userId);

    @Transactional
    void followUser(Long blogUserId, Long userId);

    @Transactional
    void unfollowUser(Long blogUserId, Long userId);
}

package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Follow;
import com.example.blog_project.domain.User;
import com.example.blog_project.repository.FollowRepository;
import com.example.blog_project.service.FollowService;
import com.example.blog_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public long getFollowerCount(Long blogUserId) {
        return followRepository.countByBlogUserId(blogUserId);
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByCurrentUserId(userId);
    }

    public boolean isFollowing(Long blogUserId, Long userId) {
        return followRepository.existsByBlogUserIdAndCurrentUserId(blogUserId, userId);
    }

    @Transactional
    public void followUser(Long blogUserId, Long userId) {
        System.out.println("service 입장");
        System.out.println(blogUserId);
        System.out.println(userId);
        if (!isFollowing(blogUserId, userId)) {
            System.out.println("팔로우 중이 아니므로 아래를 진행함");
            Follow follower = new Follow();
            User blogUser = userService.getUser(blogUserId).orElseThrow();
            User currentUser = userService.getUser(userId).orElseThrow();
            follower.setBlogUser(blogUser);
            follower.setCurrentUser(currentUser);
            followRepository.save(follower);
        }
    }

    @Transactional
    public void unfollowUser(Long blogUserId, Long userId) {
        if (isFollowing(blogUserId, userId)) {
            followRepository.deleteByBlogUserIdAndCurrentUserId(blogUserId, userId);
        }
    }
}

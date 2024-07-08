package com.example.blog_project.service;

import com.example.blog_project.domain.Follower;
import com.example.blog_project.domain.User;
import com.example.blog_project.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowerService {
    private final FollowerRepository followerRepository;
    private final UserService userService;

    public long getFollowerCount(Long userId) {
        return followerRepository.countByUserId(userId);
    }

    public long getFollowingCount(Long followerId) {
        return followerRepository.countByFollowerId(followerId);
    }

    public boolean isFollowing(Long userId, Long followerId) {
        return followerRepository.existsByUserIdAndFollowerId(userId, followerId);
    }

    public void followUser(Long userId, Long followerId) {
        System.out.println("들어오나");
        System.out.println(userId);
        System.out.println(followerId);
        if (!isFollowing(userId, followerId)) {
            System.out.println("팔로우 중이 아니므로 아래를 진행함");
            Follower follower = new Follower();
            User user = userService.getUser(userId).orElseThrow();
            User followerUser = userService.getUser(followerId).orElseThrow();
            follower.setUser(user);
            follower.setFollower(followerUser);
            followerRepository.save(follower);
        }
    }

    public void unfollowUser(Long blogId, Long followerId) {
        if (isFollowing(followerId, blogId)) {
            followerRepository.deleteByUserIdAndFollowerId(blogId, followerId);
        }
    }
}

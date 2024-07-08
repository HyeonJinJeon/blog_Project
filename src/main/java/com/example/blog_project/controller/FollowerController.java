package com.example.blog_project.controller;

import com.example.blog_project.service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowerController {

    private FollowerService followerService;

    @PostMapping("/{userId}")
    public void followUser(@PathVariable Long userId, @RequestParam Long followerId) {
        followerService.followUser(userId, followerId);
    }

    @DeleteMapping("/{userId}")
    public void unfollowUser(@PathVariable Long userId, @RequestParam Long followerId) {
        followerService.unfollowUser(userId, followerId);
    }

    @GetMapping("/status")
    public boolean isFollowing(@RequestParam Long userId, @RequestParam Long followerId) {
        return followerService.isFollowing(userId, followerId);
    }

    @GetMapping("/count/followers/{userId}")
    public long getFollowerCount(@PathVariable Long userId) {
        return followerService.getFollowerCount(userId);
    }

    @GetMapping("/count/following/{userId}")
    public long getFollowingCount(@PathVariable Long userId) {
        return followerService.getFollowingCount(userId);
    }
}


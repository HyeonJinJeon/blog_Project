package com.example.blog_project.controller;

import com.example.blog_project.service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowerController {
    private final FollowerService followerService;

    @GetMapping("/status")
    public boolean isFollowing(@RequestParam Long userId, @RequestParam Long followerId) {
        return followerService.isFollowing(followerId, userId);
    }

    @PostMapping("/{userId}")
    public void followUser(@PathVariable Long userId, @RequestParam Long followerId) {
        followerService.followUser(userId, followerId);
    }

    @DeleteMapping("/{userId}")
    public void unfollowUser(@PathVariable Long blogId, @RequestParam Long followerId) {
        System.out.println("팔로우 취소 들어오나");
        followerService.unfollowUser(blogId, followerId);
    }
}



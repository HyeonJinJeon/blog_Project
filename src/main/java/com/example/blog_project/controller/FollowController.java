package com.example.blog_project.controller;

import com.example.blog_project.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @GetMapping("/status")
    public boolean isFollowing(@RequestParam Long blogUserId, @RequestParam Long userId) {
        return followService.isFollowing(blogUserId, userId);
    }

    @PostMapping("/{blogUserId}")
    public void followUser(@PathVariable Long blogUserId, @RequestParam Long userId) {
        System.out.println("controller 입장");
        followService.followUser(blogUserId, userId);
    }

    @DeleteMapping("/{blogUserId}")
    public void unfollowUser(@PathVariable Long blogUserId, @RequestParam Long userId) {
        System.out.println("팔로우 취소 들어오나");
        followService.unfollowUser(blogUserId, userId);
    }
}



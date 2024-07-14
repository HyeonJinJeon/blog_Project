package com.example.blog_project.controller;

import com.example.blog_project.service.LikeService;
import com.example.blog_project.service.PostService;
import com.example.blog_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8082") // 클라이언트의 주소에 맞게 수정
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping("/add/like")
    public ResponseEntity addLike(@RequestParam Long postId, Authentication authentication) {
        System.out.println("좋아요 추가 들어오나");
        System.out.println(postId);
        System.out.println(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println(username);
        likeService.addLike(postId, username);

        System.out.println("좋아요 저장되었나");
        int likeCount = likeService.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", true);
        response.put("likeCount", likeCount);

        System.out.println(ResponseEntity.ok().body(response));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/remove/like")
    public ResponseEntity removeLike(@RequestParam Long postId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        likeService.removeLike(postId, username);
        int likeCount = likeService.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}
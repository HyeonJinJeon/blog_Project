package com.example.blog_project.service;

import com.example.blog_project.domain.Like;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikeService {

    void addLike(Long postId, String username);

    @Transactional
    void removeLike(Long postId, String username);

    List<Like> getLikeByPostId(Long postId);

    boolean getIsLike(User user, Post post);

    int getLikeCount(Long postId);
}

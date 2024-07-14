package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Like;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.User;
import com.example.blog_project.repository.LikeRepository;
import com.example.blog_project.repository.PostRepository;
import com.example.blog_project.repository.UserRepository;
import com.example.blog_project.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void addLike(Long postId, String username) {
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));


        System.out.println("저장하러 들어오니");
        if (!likeRepository.existsByUserAndPost(user, post)) {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void removeLike(Long postId, String username) {
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        if (likeRepository.existsByUserAndPost(user, post)) {
            likeRepository.deleteByUserAndPost(user, post);
        }
    }

    public List<Like> getLikeByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    public boolean getIsLike(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }

    public int getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}

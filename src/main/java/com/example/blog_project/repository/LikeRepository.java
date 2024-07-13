package com.example.blog_project.repository;

import com.example.blog_project.domain.Like;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository <Like, Long> {
    boolean existsByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    List<Like> findByPostId(Long postId);

    int countByPostId(Long postId);
}

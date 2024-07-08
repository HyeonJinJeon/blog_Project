package com.example.blog_project.repository;

import com.example.blog_project.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    long countByBlogUserId(Long userId);

    long countByCurrentUserId(Long followerId);

    boolean existsByBlogUserIdAndCurrentUserId(Long BlogUserId, Long CurrentUserId);

    void deleteByBlogUserIdAndCurrentUserId(Long BlogUserId, Long CurrentUserId);
}

package com.example.blog_project.repository;

import com.example.blog_project.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByBlogIdAndId(Long blogId, Long postId);

    List<Post> findBySeriesId(Long seriesId);

    @Query(value = "SELECT * FROM posts WHERE blog_id = :blogId AND (title LIKE %:keyword% OR content LIKE %:keyword%)", nativeQuery = true)
    List<Post> searchPostsByBlogAndTitleOrContent(Long blogId, String keyword);

    List<Post> findAllByOrderByCreatedAtDesc();
}

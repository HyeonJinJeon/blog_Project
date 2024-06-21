package com.example.blog_project.repository;

import com.example.blog_project.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
ㄷ
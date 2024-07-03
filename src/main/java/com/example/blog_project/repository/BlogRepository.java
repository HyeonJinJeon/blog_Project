package com.example.blog_project.repository;

import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByUser(User user);

    Blog findByUserId(Long userId);
}

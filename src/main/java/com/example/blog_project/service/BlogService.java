package com.example.blog_project.service;

import com.example.blog_project.domain.Blog;
import org.springframework.transaction.annotation.Transactional;

public interface BlogService {

    //블로그 생성
    @Transactional
    Blog createBlog(String username, String title);

    //블로그 불러오는 로직
    Blog getBlogByUserId(Long userId);

    Blog getBlogById(Long blogId);
}

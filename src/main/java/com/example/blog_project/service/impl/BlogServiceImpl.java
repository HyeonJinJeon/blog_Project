package com.example.blog_project.service.impl;

import com.example.blog_project.repository.BlogRepository;
import com.example.blog_project.repository.UserRepository;
import com.example.blog_project.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.User;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    //블로그 생성
    @Override
    @Transactional
    public Blog createBlog(String username, String title) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Blog blog = new Blog();
        blog.setUser(user);
        blog.setTitle(title);

        return blogRepository.save(blog);
    }

    //블로그 불러오는 로직
    @Override
    public Blog getBlogByUserId(Long userId) {
        return blogRepository.findByUserId(userId);
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }
}

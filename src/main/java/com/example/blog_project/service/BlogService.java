package com.example.blog_project.service;

import com.example.blog_project.repository.BlogRepository;
import com.example.blog_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.User;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    //블로그 생성
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
    public Blog getBlogByUserId(Long userId) {
        return blogRepository.findByUserId(userId);
    }
}

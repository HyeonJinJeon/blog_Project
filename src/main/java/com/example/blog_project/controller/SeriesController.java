package com.example.blog_project.controller;

import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.Series;
import com.example.blog_project.domain.User;
import com.example.blog_project.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final BlogService blogService;
    private final PostService postService;
    private final UserService userService;
    private final GetUserService getUserService;

    @GetMapping("blog/series/{title}")
    public String getSeriesDetail(@PathVariable String title, Model model, @RequestParam(value = "username") String blogUsername) {

        String username = getUserService.getUsername();
        User user = userService.findUserByUsername(username);

        User blogUser =  userService.findUserByUsername(blogUsername);
        Blog blog = blogService.getBlogByUserId(blogUser.getId());
        Long blogId = blog.getId();
        Series series = seriesService.getSeriesByTitleAndBlogId(title, blogId);

        // 블로그 게시물 처리
        if (blog != null && blog.getPosts() != null) {
            List<Post> posts = postService.getPostBySeriesId(series.getId());
            for (Post post : posts) {
//                post.setContent(postService.sanitizePostContent(post.getContent())); // 내용의 태그 제거
            }
            model.addAttribute("posts", posts);
        }

        model.addAttribute("series", series);
//        model.addAttribute("postList", postService.getPostBySeriesId(series.getId()));
        model.addAttribute("postService", postService);
        model.addAttribute("blog", blog);
        model.addAttribute("user", user);
        return "blog/series-detail";
    }
}
package com.example.blog_project.controller;

import com.example.blog_project.CookieUtil;
import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.User;
import com.example.blog_project.service.BlogService;
import com.example.blog_project.service.PostService;
import com.example.blog_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final BlogService blogService;

    //post 작성
    @GetMapping("/posts/new")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "blog/postForm";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, HttpServletRequest request, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        User user = userService.findUserByUsername(myUsername);
        Blog blog = blogService.getBlogByUserId(user.getId());
        post.setBlog(blog);

        postService.savePost(post);

        return "redirect:/blog?username=" + myUsername;
    }

    //게시글 상세보기를 위한 컨트롤러 작성
    @GetMapping("post")
    public String showPost(@RequestParam String username, @RequestParam Long blogId, @RequestParam Long postId,  Model model) {
        Post post = postService.getPostByBlogIdAndPostId(blogId, postId);
        User user = userService.findUserByUsername(username);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        return "blog/post";
    }
}

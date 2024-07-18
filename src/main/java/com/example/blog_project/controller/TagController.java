package com.example.blog_project.controller;

import com.example.blog_project.domain.Tag;
import com.example.blog_project.domain.User;
import com.example.blog_project.service.GetUserService;
import com.example.blog_project.service.PostService;
import com.example.blog_project.service.TagService;
import com.example.blog_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final PostService postService;
    private final UserService userService;
    private final GetUserService getUserService;

    @GetMapping("/posts/tag/{tagName}")
    public String showPostByTag(@PathVariable String tagName, Model model){
        String username = getUserService.getUsername();

        User user = userService.findUserByUsername(username);

        Tag tag = tagService.findByTagName(tagName);
        model.addAttribute("tag", tag);
        model.addAttribute("postService", postService);
        model.addAttribute("user", user);
        return "blog/postsByTag";
    }
}

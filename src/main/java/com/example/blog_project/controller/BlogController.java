package com.example.blog_project.controller;

import com.example.blog_project.CookieUtil;
import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.User;
import com.example.blog_project.domain.Post;
import com.example.blog_project.service.BlogService;
import com.example.blog_project.service.FollowerService;
import com.example.blog_project.service.PostService;
import com.example.blog_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final PostService postService;
    private final UserService userService;
    private final FollowerService followerService;
    private static final String UPLOAD_DIR = "/Users/jeonhyeonjin/blog_project/"; // 이미지 업로드 디렉토리

    //블로그페이지 mapping
//    @GetMapping("/blog")
//    public String my(@RequestParam(value = "username") String blogUsername, Model model, HttpServletRequest request) {
//        String myUsername = CookieUtil.getValue(request, "user");
//        User user = userService.findUserByUsername(myUsername);
//        User blogUser = userService.findUserByUsername(blogUsername);
//        Long blogUserId = blogUser.getId();
//        Blog blog = blogService.getBlogByUserId(blogUserId);
//
//        // 기본 프로필 이미지 URL 설정
//        if (blogUser.getProfileImageUrl() == null || blogUser.getProfileImageUrl().isEmpty()) {
//            blogUser.setProfileImageUrl("/Users/jeonhyeonjin/blog_project/dev-jeans.png");
//        }
//        // 블로그 게시물 처리
//        if (blog != null && blog.getPosts() != null) {
//            List<Post> posts = blog.getPosts();
//            for (Post post : posts) {
//                post.setContent(postService.sanitizePostContent(post.getContent())); // 내용의 태그 제거
//
//                // 첫 번째 이미지 URL 추출
////                String firstImageUrl = postService.extractFirstImageUrl(post.getContent());
////                post.setFirstImageUrl(firstImageUrl); // Post 클래스에 필드 추가하여 설정
//            }
//            model.addAttribute("posts", posts);
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("blogUser", blogUser);
//        model.addAttribute("blog", blog);
//        model.addAttribute("postService", postService);
//
//
//        return "blog/blog";
//    }

    //블로그페이지 mapping
    @GetMapping("/blog")
    public String my(@RequestParam(value = "username") String blogUsername, Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        User user = userService.findUserByUsername(myUsername);

        User blogUser = userService.findUserByUsername(blogUsername);
        Long blogUserId = blogUser.getId();
        Blog blog = blogService.getBlogByUserId(blogUserId);

        // 기본 프로필 이미지 URL 설정
        if (blogUser.getProfileImageUrl() == null || blogUser.getProfileImageUrl().isEmpty()) {
            blogUser.setProfileImageUrl("/Users/jeonhyeonjin/blog_project/dev-jeans.png");
        }

        // 블로그 게시물 처리
        if (blog != null && blog.getPosts() != null) {
            List<Post> posts = blog.getPosts();
            for (Post post : posts) {
                post.setContent(postService.sanitizePostContent(post.getContent())); // 내용의 태그 제거

                // 첫 번째 이미지 URL 추출
                // String firstImageUrl = postService.extractFirstImageUrl(post.getContent());
                // post.setFirstImageUrl(firstImageUrl); // Post 클래스에 필드 추가하여 설정
            }
            model.addAttribute("posts", posts);
        }

        model.addAttribute("user", user);
        model.addAttribute("blogUser", blogUser);
        model.addAttribute("blog", blog);
        model.addAttribute("postService", postService);

        long followerCount = followerService.getFollowerCount(blogUser.getId());
        long followingCount = followerService.getFollowingCount(blogUser.getId());
        boolean isFollowing = followerService.isFollowing(user.getId(), blogUser.getId());
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("isFollowing", isFollowing);

        return "blog/blog";
    }

    //헤더파일 mapping (위치 옮겨야될거같음)
    @GetMapping("/")
    public String header(@RequestParam(value = "username") String blogUsername, Model model, HttpServletRequest request) {
        String myUsername = CookieUtil.getValue(request, "user");
        User user = userService.findUserByUsername(myUsername);
        User blogUser = userService.findUserByUsername(blogUsername);
        Long blogUserId = blogUser.getId();
        Blog blog = blogService.getBlogByUserId(blogUserId);

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);


        return "fragments/header";
    }
}

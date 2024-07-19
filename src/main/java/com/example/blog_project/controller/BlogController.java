package com.example.blog_project.controller;

import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.Series;
import com.example.blog_project.domain.User;
import com.example.blog_project.domain.Post;
import com.example.blog_project.dto.PostDto;
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
public class BlogController {
    private final BlogService blogService;
    private final PostService postService;
    private final UserService userService;
    private final FollowService followService;
    private final SeriesService seriesService;
    private final GetUserService getUserService;

    //블로그 페이지 매핑
    @GetMapping("/blog")
    public String my(@RequestParam(value = "username") String blogUsername, Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        String username = getUserService.getUsername();
        User user = userService.findUserByUsername(username);
//        if (user == null) {
//            User nullUser = new User();
//            user = nullUser;
//            user.setUsername("0");
//        }

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
            List<PostDto> removeTagPosts = postService.extractPlainTextFromPosts(posts);
//            for (Post post : posts) {
//                System.out.println(post.getContent());
////                post.setContent(postService.sanitizePostContent(post.getContent())); // 내용의 태그 제거
//            }
            model.addAttribute("posts", removeTagPosts);
        }

        model.addAttribute("user", user);
        model.addAttribute("blogUser", blogUser);
        model.addAttribute("blog", blog);
        model.addAttribute("postService", postService);
        long followerCount = followService.getFollowerCount(blogUser.getId());
        long followingCount = followService.getFollowingCount(blogUser.getId());
        if(user!=null){
            boolean isFollowing = followService.isFollowing(blogUser.getId(), user.getId());
            System.out.println("isFollowing: " + isFollowing);
            model.addAttribute("isFollowing", isFollowing);
        }
        System.out.println("blogUser: " +  blogUser.getId());
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        return "blog/blog";
    }

    @GetMapping("/blog/series")
    public String getSeries(@RequestParam(value = "username") String blogUsername, Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        String username = getUserService.getUsername();

        System.out.println("시리즈 가져오나");
        User user = userService.findUserByUsername(username);

        User blogUser = userService.findUserByUsername(blogUsername);
        Long blogUserId = blogUser.getId();
        Blog blog = blogService.getBlogByUserId(blogUserId);

        // 기본 프로필 이미지 URL 설정
        if (blogUser.getProfileImageUrl() == null || blogUser.getProfileImageUrl().isEmpty()) {
            blogUser.setProfileImageUrl("/Users/jeonhyeonjin/blog_project/dev-jeans.png");
        }

        // 시리즈 목록 가져오기
        List<Series> seriesList = seriesService.getSeriesByBlogId(blog.getId());
        model.addAttribute("seriesList", seriesList);

        model.addAttribute("user", user);
        model.addAttribute("blogUser", blogUser);
        model.addAttribute("blog", blog);

        long followerCount = followService.getFollowerCount(blogUser.getId());
        long followingCount = followService.getFollowingCount(blogUser.getId());

        if (user != null){
            boolean isFollowing = followService.isFollowing(blogUser.getId(), user.getId());
            model.addAttribute("isFollowing", isFollowing);
        }

        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);

        return "blog/series";
    }

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
        model.addAttribute("blogUser", blogUser);

        long followerCount = followService.getFollowerCount(blogUser.getId());
        long followingCount = followService.getFollowingCount(blogUser.getId());
        if(user!=null){
            boolean isFollowing = followService.isFollowing(blogUser.getId(), user.getId());
            System.out.println("isFollowing: " + isFollowing);
            model.addAttribute("isFollowing", isFollowing);
        }
        System.out.println("blogUser: " +  blogUser.getId());
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        return "blog/series-detail";
    }

    //헤더 파일 매핑
    @GetMapping("/")
    public String header(Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        User user = userService.findUserByUsername(myUsername);

        Blog blog = blogService.getBlogByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        return "fragments/header";
    }
}

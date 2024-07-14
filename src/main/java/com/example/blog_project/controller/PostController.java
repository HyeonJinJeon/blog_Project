package com.example.blog_project.controller;

import com.example.blog_project.domain.*;
import com.example.blog_project.service.*;
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

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final BlogService blogService;
    private final SeriesService seriesService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final LikeService likeService;
    private final TagService tagService;

    //post 작성
    @GetMapping("/post/new")
    public String showPostForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findUserByUsername(username);
        Blog blog = blogService.getBlogByUserId(user.getId());
        List<Series> seriesList = seriesService.getSeriesByBlogId(blog.getId());
        model.addAttribute("post", new Post());
        model.addAttribute("seriesList", seriesList);
        model.addAttribute("tag", new Tag());
        return "blog/postForm";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, @RequestParam("tags") String tags, HttpServletRequest request, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();

        User user = userService.findUserByUsername(myUsername);
        Blog blog = blogService.getBlogByUserId(user.getId());
        post.setBlog(blog);

        System.out.println(tags);
        // 태그 파싱 및 설정
        Set<Tag> tagSet = tagService.parseTags(tags);
        post.setTagSet(tagSet);

        // 포스트 저장
        postService.savePost(post);

        return "redirect:/blog?username=" + myUsername;
    }


    //게시글 상세보기를 위한 컨트롤러 작성
    @GetMapping("/post")
    public String showPost(@RequestParam Long blogId, @RequestParam Long postId, Model model, Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        User user = userService.findUserByUsername(myUsername);

        Post post = postService.getPostByBlogIdAndPostId(blogId, postId);
        List<Comment> commentList = commentService.findCommentsByPostId(postId);
        Comment createComment = new Comment();
        Reply createReply = new Reply();

        List<Like> likeList = likeService.getLikeByPostId(postId);
        int likeCount = likeList.size();

        boolean isLiked = likeService.getIsLike(user, post);

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("commentList", commentList); // comments 추가
        model.addAttribute("createComment", createComment);
        model.addAttribute("createReply", createReply);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("isLiked", isLiked);

        return "blog/post";
    }

    @PostMapping("addComment")
    public String addComment(@RequestParam String username, @RequestParam Long postId, @ModelAttribute("createComment") Comment comment) {
        System.out.println("댓글 추가 들어오나");
        System.out.println(comment.getPost());
        System.out.println(comment.getUser());
        System.out.println(comment.getContent());
        User user = userService.findUserByUsername(username);
        Post post = postService.getPostById(postId);
        System.out.println(post.getBlog().getId());
        comment.setUser(user);
        comment.setPost(post);
        commentService.saveComment(comment);
        return "redirect:/post?username=" + username + "&blogId=" + post.getBlog().getId() + "&postId=" + postId;
    }

    @PostMapping("/addReply")
    public String addReply(@RequestParam String username,
                           @RequestParam Long commentId,
                           @ModelAttribute("createReply") Reply reply) {

        System.out.println("대댓글 들어오나");
        User user = userService.findUserByUsername(username);
        Comment comment = commentService.getCommentById(commentId); // 댓글 ID로부터 댓글 객체를 가져옵니다.

        reply.setUser(user);
        reply.setComment(comment);
        replyService.saveReply(reply);

        // 댓글이 속한 게시물과 블로그 정보를 가져와 리다이렉트 URL을 생성합니다.
        Long postId = comment.getPost().getId();
        Long blogId = comment.getPost().getBlog().getId();

        return "redirect:/post?username=" + username + "&blogId=" + blogId + "&postId=" + postId;
    }
}

package com.example.blog_project.controller;

import com.example.blog_project.domain.*;
import com.example.blog_project.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DraftController {
    private final DraftService draftService;
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;
    private final SeriesService seriesService;
    private final TagService tagService;

    //임시 작성 글 리스트 보여주는 곳
    @GetMapping("/blog/draft")
    public String getDrafts(@RequestParam(value = "username") String username, Authentication authentication, Model model) {
        System.out.println("임시글 들어오나");
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findUserByUsername(username);
        Blog blog = blogService.getBlogByUserId(user.getId());
        List<Draft> drafts = draftService.getDraftsByBlogId(blog.getId());
        model.addAttribute("drafts", drafts);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("postService", postService);
        return "blog/draftList"; // Return the name of the Thymeleaf template
    }

    //임시 작성 글 상세 보기
    @GetMapping("/draft")
    public String showDraft(@RequestParam Long blogId, @RequestParam Long draftId, Model model, Authentication authentication) {
        System.out.println("임시글 상세보기 들어오나");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        User user = userService.findUserByUsername(myUsername);
        Blog blog = blogService.getBlogByUserId(user.getId());

        Draft draft = draftService.getDraftsByBlogIdAndDraftId(blogId, draftId);
        model.addAttribute("draft", draft);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        return "blog/draft";
    }

    //임시 작성 글 삭제  /draft/delete
    @DeleteMapping("/draft/delete/{draftId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long draftId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        Draft draft = draftService.getDraftById(draftId);
        Map<String, String> response = new HashMap<>();

        if (draft == null) {
            response.put("redirectUrl", "/blog?username=" + myUsername);
            return ResponseEntity.ok(response);
        }

        if (draft.getBlog().getUser().getUsername().equals(authentication.getName())) {
            draftService.deleteDraft(draftId);
            response.put("redirectUrl", "/blog/draft?username=" + myUsername);
            return ResponseEntity.ok(response);
        } else {
            response.put("redirectUrl", "/");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    // 임시 작성 글 수정 및 출간
    @GetMapping("/draft/modify")
    public String showModifyPostPage(@RequestParam("draftId") Long draftId,
                                     Authentication authentication,
                                     Model model) {

        System.out.println("들어오나"+ draftId);
        Draft draft = draftService.getDraftById(draftId);
        List<Series> seriesList = seriesService.getSeriesByBlogId(draft.getBlog().getId());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();
        String tagString = tagService.makeTagsString(draft.getTagSet());

        if (draft != null && draft.getBlog().getUser().getUsername().equals(myUsername)) {
            model.addAttribute("draft", draft);
            model.addAttribute("seriesList", seriesList);
            model.addAttribute("tags", tagString);
            return "/blog/modifyDraft";
        } else {
            return "redirect:/";
        }
    }
    @PostMapping("/draft/modify")
    public String modifyPost(@ModelAttribute Draft draft,
                             @RequestParam("tags") String tags,
                             @RequestParam("action") String action,
                             HttpServletRequest request,
                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String myUsername = userDetails.getUsername();

        User user = userService.findUserByUsername(myUsername);
        Blog blog = blogService.getBlogByUserId(user.getId());
        draft.setBlog(blog);

        System.out.println(tags);
        // 태그 파싱 및 설정
        Set<Tag> tagSet = tagService.parseTags(tags);
        draft.setTagSet(tagSet);

        if ("modify".equals(action)) {
            draftService.saveDraft(draft);
            return "redirect:/blog/draft?username=" + myUsername;
        }else if ("publish".equals(action)) {
            // 임시글을 게시글로 출간하기
            postService.savePostByDraft(draft);
            draftService.deleteDraft(draft.getId());
            return "redirect:/blog/draft?username=" + myUsername;
        }else {
            return "redirect:/";
        }
    }
}

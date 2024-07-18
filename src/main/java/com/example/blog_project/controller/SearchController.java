package com.example.blog_project.controller;

import com.example.blog_project.domain.Post;
import com.example.blog_project.dto.PostDto;
import com.example.blog_project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;

    @GetMapping("/api/search")
    public List<PostDto> searchPostsByBlogAndQuery(@RequestParam("blogId") Long blogId, @RequestParam("query") String query) {
//        try {
            List<Post> searchResults = postService.searchPostsByBlogAndKeyword(blogId, query);
            List<PostDto> removeTagPosts = postService.extractPlainTextFromPosts(searchResults);
            List<PostDto> postDtoList = new ArrayList<>();

            for(PostDto post : removeTagPosts) {
                PostDto postDto = new PostDto();
                postDto.setId(post.getId());
                postDto.setTitle(post.getTitle());
                postDto.setContent(post.getContent());
                postDto.setCoverImage(post.getCoverImage());
                postDto.setCreatedAt(post.getCreatedAt());
                postDto.setBlogId(post.getBlogId());
                postDtoList.add(postDto);
            }
//            System.out.println(searchResults.get(0).getContent());
            return postDtoList;
//        }
    }
}
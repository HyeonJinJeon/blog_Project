package com.example.blog_project.service;

import com.example.blog_project.domain.Post;

import java.util.List;

public interface PostService {
    void savePost(Post post);

    //이미지 태그를 제외한 나머지 태그 모두 제거하는 로직
    String sanitizePostContent(String content);

    // post.content에 저장되어있는 이미지 중에서 첫 번째 이미지 scr 추출하는 로직
    String extractFirstImageUrl(String content);

    Post getPostByBlogIdAndPostId(Long blogId, Long postId);

    List<Post> getPostBySeriesId(Long seriesId);

    Post getPostById(Long postId);

    void deletePost(Long postId);
}

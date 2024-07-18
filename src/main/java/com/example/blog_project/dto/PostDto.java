package com.example.blog_project.dto;

import com.example.blog_project.domain.Blog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long Id;
    private String Title;
    private String Content;
    private String ImageUrl;
    private String coverImage;
    private LocalDateTime CreatedAt;
    private Long blogId;
}

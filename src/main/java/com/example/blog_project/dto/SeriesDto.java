package com.example.blog_project.dto;

import com.example.blog_project.domain.Blog;
import lombok.Data;

@Data
public class SeriesDto {
    private Long id;
    private String title;
    private Long blogID;
}

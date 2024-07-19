package com.example.blog_project.controller;

import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.Post;
import com.example.blog_project.domain.Series;
import com.example.blog_project.domain.User;
import com.example.blog_project.dto.SeriesDto;
import com.example.blog_project.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final GetUserService getUserService;
    private final UserService userService;
    private final BlogService blogService;

    @PostMapping("/api/series")
    public ResponseEntity<SeriesDto> addSeries(@RequestBody Series newSeries, @RequestParam Long blogId) {
        System.out.println("시리즈 추가 들어오냐");
        System.out.println(newSeries.getTitle());
        System.out.println(blogId);
        Blog blog = blogService.getBlogById(blogId);
        newSeries.setBlog(blog);
        Series createdSeries = seriesService.addSeries(newSeries);
        SeriesDto seriesDto = seriesService.transformSeries(createdSeries);

        return ResponseEntity.ok().body(seriesDto);
    }
}
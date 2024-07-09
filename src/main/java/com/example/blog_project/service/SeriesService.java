package com.example.blog_project.service;

import com.example.blog_project.domain.Series;
import com.example.blog_project.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;

    public List<Series> getSeriesByBlogId(Long blogId) {
        return seriesRepository.findByBlogId(blogId);
    }
}

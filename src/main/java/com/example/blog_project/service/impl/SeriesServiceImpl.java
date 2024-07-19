package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Series;
import com.example.blog_project.dto.SeriesDto;
import com.example.blog_project.repository.SeriesRepository;
import com.example.blog_project.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
    private final SeriesRepository seriesRepository;

    public List<Series> getSeriesByBlogId(Long blogId) {
        return seriesRepository.findByBlogId(blogId);
    }

    public Series getSeriesById(Long seriesId) {
        return seriesRepository.findById(seriesId).orElse(null);
    }

    public Series getSeriesByTitleAndBlogId(String title, Long blogId) {
        return seriesRepository.findByTitleAndBlogId(title, blogId);
    }

    @Override
    public Series addSeries(Series series) {
        if(seriesRepository.existsByBlogAndTitle(series.getBlog(), series.getTitle())){
            throw new IllegalArgumentException("이미 존재하는 시리즈입니다.");
        }
        return seriesRepository.save(series);
    }

    public SeriesDto transformSeries(Series series) {
        SeriesDto seriesDto = new SeriesDto();
        seriesDto.setId(series.getId());
        seriesDto.setTitle(series.getTitle());
        seriesDto.setBlogID(series.getBlog().getId());
        return seriesDto;
    }

}

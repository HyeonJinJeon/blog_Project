package com.example.blog_project.service;

import com.example.blog_project.domain.Series;
import com.example.blog_project.dto.SeriesDto;

import java.util.List;

public interface SeriesService {
    List<Series> getSeriesByBlogId(Long blogId);

    Series getSeriesById(Long seriesId);

    Series getSeriesByTitleAndBlogId(String title, Long blogId);

    Series addSeries(Series newSeries);

    SeriesDto transformSeries(Series series);
}

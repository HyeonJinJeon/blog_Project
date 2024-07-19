package com.example.blog_project.repository;

import com.example.blog_project.domain.Blog;
import com.example.blog_project.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByBlogId(Long blogId);

    Series findByTitleAndBlogId(String seriesName, Long blogId);

    boolean existsByBlogAndTitle(Blog blog, String title);
}

package com.example.blog_project.repository;

import com.example.blog_project.domain.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findByBlogId(Long id);

    Draft findByBlogIdAndId(Long blogId, Long draftId);
}

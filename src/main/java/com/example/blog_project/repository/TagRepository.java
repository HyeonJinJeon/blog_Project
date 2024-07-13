package com.example.blog_project.repository;

import com.example.blog_project.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String tagName);

    Boolean existsByName(String tagName);
}

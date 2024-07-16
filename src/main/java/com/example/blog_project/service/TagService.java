package com.example.blog_project.service;

import com.example.blog_project.domain.Tag;

import java.util.Set;

public interface TagService {
    Tag findByTagName(String name);

    void addTag(Tag tag);

    Set<Tag> parseTags(String tags);

    String makeTagsString(Set<Tag> tagSet);
}

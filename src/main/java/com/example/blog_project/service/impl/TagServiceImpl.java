package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Tag;
import com.example.blog_project.repository.TagRepository;
import com.example.blog_project.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    public Tag findByTagName(String name) {
        return tagRepository.findByName(name);
    }

    public void addTag(Tag tag) {
        if (tagRepository.findByName(tag.getName()) == null) {
            tagRepository.save(tag);
        }
    }

    public Set<Tag> parseTags(String tags) {
        String[] tagNames = tags.split(",");
        Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            System.out.println(tagName);
            Tag tag = findByTagName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                addTag(tag);
            }
            tagSet.add(tag);
        }
        System.out.println(tagSet.toArray());

        return tagSet;
    }
}

package com.example.blog_project.service;

import com.example.blog_project.domain.Draft;
import com.example.blog_project.domain.Post;

import java.util.List;

public interface DraftService {
    void saveDraft(Draft draft);

    void saveDraftByPost(Post post);

    List<Draft> getDraftsByBlogId(Long blogId);

    Draft getDraftsByBlogIdAndDraftId(Long blogId, Long draftId);

    Draft getDraftById(Long draftId);

    void deleteDraft(Long draftId);
}

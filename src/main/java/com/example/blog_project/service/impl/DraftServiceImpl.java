package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Draft;
import com.example.blog_project.domain.Post;
import com.example.blog_project.repository.DraftRepository;
import com.example.blog_project.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {
    private final DraftRepository draftRepository;

    @Override
    public void saveDraftByPost(Post post) {
        Draft draft = new Draft();
        draft.setBlog(post.getBlog());
        draft.setSeries(post.getSeries());
        draft.setTitle(post.getTitle());
        draft.setContent(post.getContent());
        draft.setTagSet(post.getTagSet());
        draftRepository.save(draft);
    }

    @Override
    public void saveDraft(Draft draft) {
        draftRepository.save(draft);
    }

    @Override
    public List<Draft> getDraftsByBlogId(Long blogId) {
        return draftRepository.findByBlogId(blogId);
    }

    @Override
    public Draft getDraftsByBlogIdAndDraftId(Long blogId, Long draftId) {
        return draftRepository.findByBlogIdAndId(blogId, draftId);
    }

    @Override
    public Draft getDraftById(Long draftId) {
        return draftRepository.findById(draftId).orElse(null);
    }

    @Override
    public void deleteDraft(Long draftId) {
        draftRepository.deleteById(draftId);
    }

}

package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Comment;
import com.example.blog_project.repository.CommentRepository;
import com.example.blog_project.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    public List<Comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }
}

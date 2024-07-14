package com.example.blog_project.service;

import com.example.blog_project.domain.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByPostId(Long postId);

    void saveComment(Comment comment);

    Comment getCommentById(Long commentId);
}

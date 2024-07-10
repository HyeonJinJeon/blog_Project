package com.example.blog_project.service;

import com.example.blog_project.domain.Reply;
import com.example.blog_project.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    public void saveReply(Reply reply) {
        replyRepository.save(reply);
    }
}

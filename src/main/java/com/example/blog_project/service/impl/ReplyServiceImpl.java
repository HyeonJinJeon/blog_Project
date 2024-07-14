package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Reply;
import com.example.blog_project.repository.ReplyRepository;
import com.example.blog_project.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    public void saveReply(Reply reply) {
        replyRepository.save(reply);
    }
}

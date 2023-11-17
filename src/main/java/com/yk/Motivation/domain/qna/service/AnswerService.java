package com.yk.Motivation.domain.qna.service;

import com.yk.Motivation.domain.qna.entity.Answer;
import com.yk.Motivation.domain.qna.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void save(Answer answer) {
        this.answerRepository.save(answer);
    }
}

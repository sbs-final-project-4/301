package com.yk.Motivation.domain.qna.service;

import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.qna.entity.Answer;
import com.yk.Motivation.domain.qna.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public RsData<Answer> save(Answer answer) {
        answerRepository.save(answer);
        return new RsData<>("S-1", "답변이 등록 되었습니다.", null);
    }
}

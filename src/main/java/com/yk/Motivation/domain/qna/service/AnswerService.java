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

    /*public void save(Answer answer) {
        this.answerRepository.save(answer);
    }*/

    public RsData<Answer> save(Answer answer) {
        answerRepository.save(answer);
        return new RsData<>("S-1", answer.getId() + "번 댓글이 생성되었습니다.", null);
    }
}

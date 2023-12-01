package com.yk.Motivation.domain.qna.service;

import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.comment.entity.Comment;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public Page<Question> getList(Pageable pageable) {
        return this.questionRepository.findAll(pageable);
    }

    public Page<Question> getListWithPriority(long lectureId, long lessonId, Member currentMember, Pageable pageable) {
        return questionRepository.findWithPriority(lectureId, lessonId, currentMember.getId(), pageable);
    }

    public boolean hasAnswersByOthers(Question question, Member currentMember) {
        return question.getAnswerList().stream()
                .anyMatch(answer -> answer.getMember().getGrantedAuthorities()
                        .stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("producer")));
    }

    public Question getQuestion(Long id) {
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question with id " + id + " not found"));
    }

    public RsData<Question> create(Question question) {
        questionRepository.save(question);
        return new RsData<>("S-1", question.getId() + "번 질문이 생성되었습니다.", null);
    }

    @Transactional
    public RsData<?> remove(Question question) {
        questionRepository.delete(question);
        return new RsData<>("S-1", question.getId() + "번 질문이 삭제되었습니다.", null);
    }

    @Transactional
    public RsData<Question> save(Question question) {
        questionRepository.save(question);
        return new RsData<>("S-1", question.getId() + "번 질문이 수정되었습니다.", null);
    }

    public RsData<?> checkActorCanModify(Member member, Question question) {
        if (member == null || !member.equals(question.getMember())) {
            return new RsData<>("F-1", "권한이 없습니다.", null);
        }

        return new RsData<>("S-1", "가능합니다.", null);
    }

    public List<Question> findbyTop3Questions() {
        return questionRepository.findTop3ByOrderByIdDesc();
    }
}

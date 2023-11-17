package com.yk.Motivation.domain.qna.controller;

import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.qna.entity.Answer;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.service.AnswerService;
import com.yk.Motivation.domain.qna.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usr/qna/a")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final Rq rq;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id, @RequestParam String content) {
        Member loginedMember = rq.getMember();

        Question question = this.questionService.getQuestion(id);

        // 새 답변 객체를 생성하고 정보 설정
        Answer answer = new Answer();
        answer.setMember(loginedMember); // 답변에 로그인된 회원 설정
        answer.setContent(content);      // 답변 내용 설정
        answer.setQuestion(question);    // 답변과 관련된 질문 설정

        // 답변을 데이터베이스에 저장하는 로직
        this.answerService.save(answer);

        return String.format("redirect:/usr/qna/q/detail/%s", id);
    }

    // video in qna comment create
    @PostMapping("/videoInCreateAnswer/{id}")
    public String videoInCreateAnswer(Model model, @PathVariable("id") Long id, @RequestParam String content, @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Member loginedMember = rq.getMember();

        Question question = this.questionService.getQuestion(id);

        // 새 답변 객체를 생성하고 정보 설정
        Answer answer = new Answer();
        answer.setMember(loginedMember); // 답변에 로그인된 회원 설정
        answer.setContent(content);      // 답변 내용 설정
        answer.setQuestion(question);    // 답변과 관련된 질문 설정

        // 답변을 데이터베이스에 저장하는 로직
        this.answerService.save(answer);

        return String.format("redirect:/usr/qna/q/videoInDetail/%s?lessonId=%d", id, lessonId);
    }
}

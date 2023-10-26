package com.yk.Motivation.domain.lecture.service;

import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.document.service.DocumentService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.repository.LectureRepository;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final DocumentService documentService;


    @Transactional
    public RsData<Lecture> write(Member author, String subject, String tagsStr, String body, boolean isPublic) {
        return write(author, subject, tagsStr, body, Ut.markdown.toHtml(body), isPublic);
    }

    @Transactional
    public RsData<Lecture> write(Member producer, String subject, String tagsStr, String body, String bodyHtml, boolean isPublic) {
        Lecture lecture = Lecture.builder()
                .producer(producer)
                .subject(subject)
                .body(body)
                .bodyHtml(bodyHtml)
                .isPublic(isPublic)
                .build();

        lectureRepository.save(lecture);

        lecture.addTags(tagsStr);

        documentService.updateTempGenFilesToInBody(lecture);

        return new RsData<>("S-1", lecture.getId() + "번 게시물이 생성되었습니다.", lecture);
    }


    public Page<Lecture> findByKw(String kwType, String kw, Pageable pageable) {
        return lectureRepository.findByKw(kwType, kw, pageable);
    }
}

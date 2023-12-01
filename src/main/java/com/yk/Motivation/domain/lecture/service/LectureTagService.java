package com.yk.Motivation.domain.lecture.service;

import com.yk.Motivation.domain.lecture.entity.LectureTag;
import com.yk.Motivation.domain.lecture.repository.LectureTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureTagService {

    private final LectureTagRepository lectureTagRepository;

    public List<LectureTag> findAll() {
        return lectureTagRepository.findAll();
    }

}

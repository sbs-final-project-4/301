package com.yk.Motivation.domain.lecture.repository;

import com.yk.Motivation.domain.lecture.entity.LectureTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureTagRepository extends JpaRepository<LectureTag, Long> {
}

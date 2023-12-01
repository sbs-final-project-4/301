package com.yk.Motivation.domain.lecture.repository;

import com.yk.Motivation.domain.lecture.entity.LectureTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureTagRepository extends JpaRepository<LectureTag, Long> {
}

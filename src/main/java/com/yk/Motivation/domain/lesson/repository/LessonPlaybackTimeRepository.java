package com.yk.Motivation.domain.lesson.repository;

import com.yk.Motivation.domain.lesson.entity.LessonPlaybackTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonPlaybackTimeRepository extends JpaRepository<LessonPlaybackTime, Long> {
    Optional<LessonPlaybackTime> findByMemberIdAndLessonId(Long memberId, Long lessonId);
}

package com.yk.Motivation.domain.lesson.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final GenFileService genFileService;


    @Transactional
    public RsData<Lecture> write(Lecture lecture, List<String> subjects, List<MultipartFile> videos) {

        for (String subject : subjects) {

            Lesson lesson = Lesson.builder()
                    .lecture(lecture)
                    .subject(subject)
                    .build();

            lessonRepository.save(lesson);
        }

        for (MultipartFile videoFile : videos) {

            saveVideoFile(lecture, videoFile, 0);

        }

        return new RsData<>("S-1", lecture.getSubject() + " 강의의 커리큘럼이 생성되었습니다.", lecture);
    }










    @Transactional
    public RsData<GenFile> saveVideoFile(Lecture lecture, MultipartFile attachmentFile, long fileNo) {
        String attachmentFilePath = Ut.file.toFile(attachmentFile, AppConfig.getTempDirPath());
        return saveVideoFile(lecture, attachmentFilePath, fileNo);
    }

    @Transactional
    public RsData<GenFile> saveVideoFile(Lecture lecture, String attachmentFile, long fileNo) {
        GenFile genFile = genFileService.saveForLesson(lecture.getModelName(), lecture.getId(), "common", "attachment", fileNo, attachmentFile);

        return new RsData<>("S-1", genFile.getId() + "번 파일이 생성되었습니다.", genFile);
    }



}

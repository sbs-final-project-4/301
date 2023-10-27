package com.yk.Motivation.domain.lesson.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.ffmpeg.service.FfmpegService;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.repository.LessonRepository;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final GenFileService genFileService;
    private final FfmpegService ffmpegService;


    @Transactional
    public RsData<Lecture> write(Lecture lecture, List<String> subjects, List<MultipartFile> videos) {

        for (int i = 0; i < subjects.size(); i++) {

            Lesson lesson = Lesson.builder()
                    .lecture(lecture)
                    .subject(subjects.get(i))
                    .sortNo(i+1)
                    .build();

            lessonRepository.save(lesson);

            RsData<GenFile> rsData = saveVideoFile(lesson, videos.get(i), 1);

            CompletableFuture.runAsync(() -> {
                try {
                    ffmpegService.videoHlsMake(rsData.getData().getFilePath(), rsData.getData().getFileDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return new RsData<>("S-1", lecture.getSubject() + " 강의의 커리큘럼이 생성되었습니다.", lecture);
    }







    @Transactional
    public RsData<GenFile> saveVideoFile(Lesson lesson, MultipartFile attachmentFile, long fileNo) {
        String attachmentFilePath = Ut.file.toFile(attachmentFile, AppConfig.getTempDirPath());
        return saveVideoFile(lesson, attachmentFilePath, fileNo);
    }

    @Transactional
    public RsData<GenFile> saveVideoFile(Lesson lesson, String attachmentFile, long fileNo) {
        GenFile genFile = genFileService.saveForLesson(lesson.getModelName(), lesson.getId(), "common", "lessonVideo", fileNo, attachmentFile);

        return new RsData<>("S-1", genFile.getId() + "번 파일이 생성되었습니다.", genFile);
    }


    public Optional<Lesson> findById(Long id){
        return lessonRepository.findById(id);
    }

    public List<Lesson> findByLectureId(Long id) {
        return lessonRepository.findByLectureId_Id(id);

    }



}

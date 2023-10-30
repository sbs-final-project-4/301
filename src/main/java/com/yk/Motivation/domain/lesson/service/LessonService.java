package com.yk.Motivation.domain.lesson.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.ffmpeg.service.FfmpegService;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.service.LectureService;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.repository.LessonRepository;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    @Autowired
    @Lazy
    private LessonService self;

    private final LessonRepository lessonRepository;
    private final LectureService lectureService;
    private final GenFileService genFileService;
    private final FfmpegService ffmpegService;


    @Transactional
    public RsData<Lecture> write(Lecture lecture, List<String> subjects, List<MultipartFile> videos) {

        if (lecture.isLessonsReady()) {
            lecture.setLessonsReady(false);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < subjects.size(); i++) {

            Lesson lesson = Lesson.builder()
                    .lecture(lecture)
                    .subject(subjects.get(i))
                    .sortNo(i + 1)
                    .build();

            lessonRepository.save(lesson);

            RsData<GenFile> rsData = saveVideoFile(lesson, videos.get(i), 0);

            CompletableFuture<Void> processFuture = CompletableFuture.runAsync(() -> {
                try {
                    double originalDuration = ffmpegService.videoHlsMake(rsData.getData().getFilePath(), rsData.getData().getFileDir());
                    self.setLessonLength(lesson.getId(), originalDuration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            futures.add(processFuture);
        }

//         모든 비디오 처리 작업이 완료될 때까지 기다린 후
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    self.setLessonsReadyTrue(lecture);
                });

        // 응답 바로 return
        return new RsData<>("S-1", lecture.getSubject() + " 강의의 커리큘럼이 생성되었습니다.", lecture);
    }

    @Transactional
    public void modify(Lecture lecture, List<String> subject, List<MultipartFile> video) {

        if (lecture.isLessonsReady()) {
            lecture.setLessonsReady(false);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<Lesson> lessons = lecture.getLessons();

        for (int i = 0; i < lessons.size(); i++) {

            Lesson lesson = lessons.get(i);

            lesson.setSubject(subject.get(i));

            if(!video.get(i).isEmpty()) {
                removeVideoFile(lesson, 1);

                RsData<GenFile> rsData = saveVideoFile(lesson, video.get(i), 0);

                CompletableFuture<Void> processFuture = CompletableFuture.runAsync(() -> {
                    try {
                        double originalDuration = ffmpegService.videoHlsMake(rsData.getData().getFilePath(), rsData.getData().getFileDir());
                        self.setLessonLength(lesson.getId(), originalDuration);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                futures.add(processFuture);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRunAsync(() -> {
                        self.setLessonsReadyTrue(lecture);
                    });
        }
    }


    @Transactional
    public void setLessonLength(long lessonId, double originalDuration) {

        Lesson lesson = findById(lessonId).get();

        lesson.setLessonLength((int) originalDuration);
    }

    @Transactional
    public void setLessonsReadyTrue(Lecture lecture) {

        Lecture saveLecture = lectureService.findById(lecture.getId()).get();

        if (!saveLecture.isLessonsReady()) {
            saveLecture.setLessonsReady(true);
        }
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

    @Transactional
    public void removeVideoFile(Lesson lesson, long fileNo) {
        genFileService.removeLessonVideo(lesson.getModelName(), lesson.getId(), "common", "lessonVideo", fileNo);
    }

    public Optional<Lesson> findById(Long id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> findByLectureId(Long id) {
        return lessonRepository.findByLectureId_Id(id);

    }


}

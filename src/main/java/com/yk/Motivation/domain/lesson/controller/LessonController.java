package com.yk.Motivation.domain.lesson.controller;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.controller.ArticleController;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.controller.LectureController;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.service.LectureService;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.service.LessonService;
import com.yk.Motivation.standard.util.Ut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usr/lesson")
@RequiredArgsConstructor
@Validated
public class LessonController {

    private final LessonService lessonService;
    private final LectureService lectureService;
    private final GenFileService genFileService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{lectureId}/write")
    public String showWrite(
            Model model,
            @PathVariable Long lectureId
    ) {
        Lecture lecture = lectureService.findById(lectureId).get();

        model.addAttribute("lecture", lecture);

        return "usr/lesson/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{lectureId}/write")
    public String write(
            Model model,
            @PathVariable Long lectureId,
            @Valid LessonController.LessonWriteForm writeForm
    ) {
        Lecture lecture = lectureService.findById(lectureId).get();

        RsData<Lecture> rsData = lessonService.write(lecture, writeForm.getSubjects(), writeForm.getVideos());

        return rq.redirectOrBack("/usr/lecture/detail/%d".formatted(lectureId), rsData);
    }

    @Setter
    @Getter
    public static class LessonWriteForm {
        private List<String> subjects;
        private List<MultipartFile> videos;
    }



















    @GetMapping("/hls/{id}")
    public String videoHls(
            Model model,
            @PathVariable Long lessonId
            ) {

        Lesson lesson = lessonService.findById(lessonId).get();
        GenFile genFile = genFileService.findBy(lesson.getModelName(), lesson.getId(), "common", "lessonVideo", 1).get();

        String masterPlayListPath = AppConfig.getGenFileDirPath() + "/" + genFile.getFileDir() +"/" + "hls" + "/" + "master.m3u8";

        model.addAttribute("videoUrl", masterPlayListPath);
        return "hls";
    }

    @GetMapping("/hls/{fileName}.m3u8")
    public ResponseEntity<Resource> videoHlsMasterM3U8(@PathVariable String fileName) {

        String fileFullPath = outputFilePath + "/" + fileName + ".m3u8";
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".m3u8");
        headers.setContentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"));

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/hls/{folderName2}/{fileName}.m3u8")
    public ResponseEntity<Resource> videoHlsMediaM3U8(@PathVariable String folderName2, @PathVariable String fileName) {

        String fileFullPath = outputFilePath + "/" + folderName2 + "/" + fileName + ".m3u8";
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".m3u8");
        headers.setContentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"));

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/hls/{folderName2}/{tsName}.ts")
    public ResponseEntity<Resource> videoHlsTs(@PathVariable String folderName2, @PathVariable String tsName) {
        String fileFullPath = outputFilePath + "/" + folderName2 + "/" + tsName + ".ts";
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tsName + ".ts");
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }


}

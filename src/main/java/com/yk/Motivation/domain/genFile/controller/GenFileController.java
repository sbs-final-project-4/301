package com.yk.Motivation.domain.genFile.controller;

import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.standard.util.Ut;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/genFile")
public class GenFileController {
    private final GenFileService genFileService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable long id, HttpServletRequest request) throws FileNotFoundException {
        GenFile genFile = genFileService.findById(id).get();
        String filePath = genFile.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath)); // 파일 경로에서 FileInputStream 객체 생성하고 InputStreamResource 로

        String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath()); // file 의 MIME 타입 결정 ( text/html , image/jpeg, video/mp4 ...)

        if (contentType == null) contentType = "application/octet-stream"; // MIME 타입 기본값

        String fileName = Ut.url.encode(genFile.getOriginFileName()).replaceAll("\\+", " "); // 파일 이름 URL 인코딩 후 + 를 공백으로

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"") // 파일을 첨부로 다운로드 할 것 (헤더 설정)
                .contentType(MediaType.parseMediaType(contentType)).body(resource); // HTTP 응답의 본문 ( resource 객체의 파일을 클라이언트에게 전송)
    }
}
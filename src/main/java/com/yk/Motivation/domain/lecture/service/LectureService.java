package com.yk.Motivation.domain.lecture.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.document.service.DocumentService;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.repository.LectureRepository;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.entity.LessonPlaybackTime;
import com.yk.Motivation.domain.lesson.service.LessonService;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.product.entity.Product;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final DocumentService documentService;
    private final GenFileService genFileService;

    private static final Map<String, String> thumbnailMap = new LinkedHashMap<>();

    static {
        thumbnailMap.put("spring", "https://cdn.inflearn.com/wp-content/uploads/spring_ver.2018-1.jpg");
        thumbnailMap.put("스프링", thumbnailMap.get("spring"));
        thumbnailMap.put("java", "https://s3.ap-northeast-2.amazonaws.com/grepp-cloudfront/programmers_imgs/learn/thumb-course-java-intermediate.jpg");
        thumbnailMap.put("자바", thumbnailMap.get("java"));
        thumbnailMap.put("jsp", "https://cdn.inflearn.com/wp-content/uploads/jsp_ver.2018-1.jpg");
        thumbnailMap.put("제이에스피", thumbnailMap.get("jsp"));
        thumbnailMap.put("react", "https://cdn.inflearn.com/public/courses/326905/cover/739f7b4b-1a9f-478f-a6a8-1a13bf58cae3/326905-eng.png");
        thumbnailMap.put("리액트", thumbnailMap.get("react"));
        thumbnailMap.put("node", "https://cdn.inflearn.com/public/courses/331324/cover/f09739a9-e3aa-4170-b630-6b9f58134c01/331324-eng.png");
        thumbnailMap.put("node.js", thumbnailMap.get("node"));
        thumbnailMap.put("html", "https://cdn.inflearn.com/public/courses/327351/cover/44676d5f-95fc-46eb-baea-6c344bdcb2e2/html-css-tumbnail2.jpg");
        thumbnailMap.put("css", thumbnailMap.get("html")); // HTML과 CSS는 같은 이미지 사용
        thumbnailMap.put("php", "https://cdn.inflearn.com/wp-content/uploads/php001.jpg");
        thumbnailMap.put("c", "https://cdn.inflearn.com/public/courses/330182/cover/e15823cf-d096-43b5-a182-a2345d1fcd7b/330182-eng.jpg");
    }

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

        return new RsData<>("S-1", lecture.getId() + "번 강의가 생성되었습니다.", lecture);
    }

    @Transactional
    public void addProduct(Long lectureId, Product product) {
        Lecture lecture = findById(lectureId).get();
        lecture.setProduct(product);
    }

    @Transactional
    public RsData<Lecture> modify(Lecture lecture, String subject, String tagsStr, String body, String bodyHtml, boolean isPublic) {

        lecture.modifyTags(tagsStr);

        lecture.setSubject(subject);
        lecture.setBody(body);
        lecture.setBodyHtml(bodyHtml);
        lecture.setPublic(isPublic);

        documentService.updateTempGenFilesToInBody(lecture);

        return new RsData<>("S-1", lecture.getId() + "번 게시물이 수정되었습니다.", lecture);
    }

    public RsData<?> remove(Lecture lecture) {
        findGenFiles(lecture).forEach(genFileService::remove);

        lectureRepository.delete(lecture);

        return new RsData<>("S-1", lecture.getId() + "번 게시물이 삭제되었습니다.", null);
    }







    @Transactional
    public RsData<GenFile> saveAttachmentFile(Lecture lecture, MultipartFile attachmentFile, long fileNo) {
        String attachmentFilePath = Ut.file.toFile(attachmentFile, AppConfig.getTempDirPath());
        return saveAttachmentFile(lecture, attachmentFilePath, fileNo);
    }

    @Transactional
    public RsData<GenFile> saveAttachmentFile(Lecture lecture, String attachmentFile, long fileNo) {
        GenFile genFile = genFileService.save(lecture.getModelName(), lecture.getId(), "common", "attachment", fileNo, attachmentFile);

        return new RsData<>("S-1", genFile.getId() + "번 파일이 생성되었습니다.", genFile);
    }

    @Transactional
    public void removeAttachmentFile(Lecture lecture, long fileNo) {
        genFileService.remove(lecture.getModelName(), lecture.getId(), "common", "attachment", fileNo);
    }

    public String getThumbnailImgUrl(String subject) {
        String subjectLower = subject.toLowerCase();

        for (Map.Entry<String, String> entry : thumbnailMap.entrySet()) {
            if (subjectLower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "/resource/common/videoImg.png"; // 기본 섬네일
    }



    private List<GenFile> findGenFiles(Lecture lecture) {
        return genFileService.findByRelId(lecture.getModelName(), lecture.getId());
    }

    public Map<String, GenFile> findGenFilesMapKeyByFileNo(Lecture lecture, String typeCode, String type2Code) {
        return genFileService.findGenFilesMapKeyByFileNo(lecture.getModelName(), lecture.getId(), typeCode, type2Code);
    }

    public Page<Lecture> findByKw(String kwType, String kw, Pageable pageable) {
        return lectureRepository.findByKw(kwType, kw, pageable);
    }

    public Optional<Lecture> findById(long id) {
        return lectureRepository.findById(id);
    }

    public Page<Lecture> findByTag(String tagContent, Pageable pageable) {
        return lectureRepository.findByLectureTags_contentAndIsPublicTrue(tagContent, pageable);
    }

    public List<Lecture> findAllByProducerId(Long id) {
        return lectureRepository.findByProducerId(id);
    }

    public List<Lecture> findTop4Lectures() {
        return lectureRepository.findTop4ByOrderByIdDesc();
    }






    public RsData<?> checkProducerCanModify(Member producer, Lecture lecture) {
        if (producer == null || !producer.equals(lecture.getProducer())) {
            return new RsData<>("F-1", "권한이 없습니다.", null);
        }

        return new RsData<>("S-1", "가능합니다.", null);
    }

    public RsData<?> checkProducerCanRemove(Member producer, Lecture lecture) {
        return checkProducerCanModify(producer, lecture);
    }

}

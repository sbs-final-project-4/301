package com.yk.Motivation.domain.genFile.service;

import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.repository.GenFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.yk.Motivation.standard.util.Ut;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenFileService {
    private final GenFileRepository genFileRepository;

    // 조회
    public Optional<GenFile> findGenFileBy(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo) {
        return genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);
    }

    // 명령
    @Transactional
    public GenFile save(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, MultipartFile multipartFile) {
        findGenFileBy(relTypeCode, relId, typeCode, type2Code, fileNo).ifPresent(genFile -> {
            Ut.file.remove(genFile.getFilePath());
            genFileRepository.delete(genFile);
        });

        String originFileName = multipartFile.getOriginalFilename(); // 확장자 포함한 원본 파일의 이름
        String fileExt = Ut.file.getExt(originFileName); // 파일의 확장자
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt); // img, video, audio 등...
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt); // 파일의 확장자?
        int fileSize = (int) multipartFile.getSize(); // 파일의 크기를 바이트 단위로
        String fileDir = getCurrentDirName(relTypeCode); // relTypeCode/2023_10_11 ...

        GenFile genFile = GenFile.builder()
                .relTypeCode(relTypeCode) // 관련 엔티티 이름
                .relId(relId) // 관련 엔티티 id
                .typeCode(typeCode) // common...
                .type2Code(type2Code) // profileImg...
                .fileExtTypeCode(fileExtTypeCode)
                .fileExtType2Code(fileExtType2Code)
                .originFileName(originFileName)
                .fileSize(fileSize)
                .fileNo(fileNo)
                .fileExt(fileExt)
                .fileDir(fileDir)
                .build();

        genFileRepository.save(genFile);

        File file = new File(genFile.getFilePath()); // directory 핸들링

        file.getParentFile().mkdirs(); // 없으면 생성

        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return genFile;
    }

    private String getCurrentDirName(String relTypeCode) { // relTypeCode/2023_10_11 ...
        return relTypeCode + "/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

}
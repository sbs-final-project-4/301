package com.yk.Motivation.domain.genFile.entity;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
@Table(indexes = {
        @Index(name = "idx1", columnList = "relId,relTypeCode,typeCode,type2Code")
})
public class GenFile extends BaseEntity {
    private String relTypeCode; // 관련 Entity 이름
    private long relId; // 관련 Entity Id
    private String typeCode; // common ...
    private String type2Code; // profileImg ...
    private String fileExtTypeCode; // img, video, mp4 ...
    private String fileExtType2Code; // 파일 확장자?
    private long fileSize; // file 의 크기 (바이트)
    private int fileNo; // file Number
    private String fileExt; // 파일 확장자?
    private String fileDir; // relTypeCode/2023_10_11 ...
    private String originFileName; // 원본 file 이름 (확장자 포함)

    public String getFileName() {
        return getId() + "." + getFileExt();
    }

    public String getUrl() {
        return "/gen/" + getFileDir() + "/" + getFileName();
    }

    public String getDownloadUrl() {
        return "/usr/genFile/download/" + getId();
    }

    public String getFilePath() {
        return AppConfig.getGenFileDirPath() + "/" + getFileDir() + "/" + getFileName();
    }

}
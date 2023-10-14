package com.yk.Motivation.domain.attr.entity;

import com.yk.Motivation.base.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        indexes = {
                // 변수명이 같은 데이터 생성되는것을 막는 역할
                // 특정 변수명으로 검색했을 때 초고속 검색이 되도록
                @Index(name = "idx1", columnList = "relId, relTypeCode, typeCode, type2Code", unique = true),
                // 특정 그룹의 데이터들을 불러올 때
                @Index(name = "idx2", columnList = "relTypeCode, typeCode, type2Code")
        }
)
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Attr extends BaseEntity {
    private String relTypeCode;
    private long relId;
    private String typeCode;
    private String type2Code;
    private String val; // value
    private LocalDateTime expireDate;
}
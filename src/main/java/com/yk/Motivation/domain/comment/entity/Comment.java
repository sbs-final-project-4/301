package com.yk.Motivation.domain.comment.entity;

import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.standard.util.DateUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity  // JPA Entity로 선언
@Setter  // Lombok: Setter 자동 생성
@Getter  // Lombok: Getter 자동 생성
@AllArgsConstructor(access = PROTECTED)  // 모든 필드 값을 인자로 받는 생성자 생성
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder  // Lombok: Builder 패턴 구현
@ToString(callSuper = true)  // Lombok: toString 메서드 오버라이드
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    private Integer rating = 0; // 별점 0~5 (기본값 0으로 설정)

    private String nickName; // member는 nickname만 기록

    public String getFormattedDate() {
        // BaseEntity의 createDate를 사용
        return DateUtils.timeForToday(this.getCreateDate());
    }
}

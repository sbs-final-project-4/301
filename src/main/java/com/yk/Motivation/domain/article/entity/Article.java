package com.yk.Motivation.domain.article.entity;


import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.board.entity.Board;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.yk.Motivation.domain.member.entity.Member;


import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Article extends BaseEntity {
    @ManyToOne
    private Member author;

    @ManyToOne
    private Board board;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String bodyHtml;

    public String getBodyForEditor() {
        return body
                .replaceAll("(?i)(</?)script", "$1t-script");
    }

    public String getBodyHtmlForPrint() {
        return bodyHtml
                .replaceAll("toastui-editor-ww-code-block-highlighting", "");
    }
}
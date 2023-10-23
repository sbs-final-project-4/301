package com.yk.Motivation.domain.article.entity;


import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.standard.util.Ut;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.yk.Motivation.domain.member.entity.Member;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = {CascadeType.ALL})
    @Builder.Default
    @ToString.Exclude
    private Set<ArticleTag> articleTags = new HashSet<>();

    public String getTagsStr() {
        if (articleTags.isEmpty()) return "";

        return "#" + articleTags
                .stream()
                .map(ArticleTag::getContent)
                .collect(Collectors.joining(" #"));
    }

    public void addTag(String tagContent) {
        ArticleTag articleTag = ArticleTag
                .builder()
                .article(this)
                .author(this.author)
                .content(tagContent)
                .build();

        articleTags.add(articleTag);
    }

    public void removeTag(String tagContent) {
        articleTags.removeIf(articleTag -> articleTag.getContent().equals(tagContent));
    }

    public String getBodyForEditor() {
        return body
                .replaceAll("(?i)(</?)script", "$1t-script");
    }

    public String getBodyHtmlForPrint() {
        return bodyHtml
                .replace("toastui-editor-ww-code-block-highlighting", "");
    }

    public void addTags(String tagsStr) {
        Arrays.stream(tagsStr.split("#|,"))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet())
                .forEach(this::addTag);
    }

    public void modifyTags(String newTagsStr) {
        Set<String> newTags = Arrays.stream(newTagsStr.split("#|,"))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());

        // articleTags 에서 newTagsStr 에 없는 것들은 삭제
        articleTags.removeIf(articleTag -> !newTags.contains(articleTag.getContent()));

        addTags(newTagsStr);
    }

    public String getTagLinks(String linkTemplate, String urlTemplate) {
        if (articleTags.isEmpty()) return "-";

        final String finaLinkTemplate = linkTemplate.replace("`", "\"");

        return articleTags
                .stream()
                .map(articleTag -> finaLinkTemplate
                        .formatted(urlTemplate.formatted(Ut.url.encode(articleTag.getContent())), articleTag.getContent()))
                .sorted()
                .collect(Collectors.joining(" "));
    }
}
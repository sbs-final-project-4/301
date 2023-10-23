package com.yk.Motivation.domain.postKeyword.repository;

import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.postKeyword.entity.PostKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long> {
    Optional<PostKeyword> findByAuthorAndContent(Member author, String content);
}
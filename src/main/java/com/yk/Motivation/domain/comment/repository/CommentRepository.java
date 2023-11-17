package com.yk.Motivation.domain.comment.repository;

import com.yk.Motivation.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleId(Long articleId);

    // 페이징 처리를 위한 메소드
    Page<Comment> findByArticleId(long articleId, Pageable pageable);
}

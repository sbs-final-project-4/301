package com.yk.Motivation.domain.comment.service;

import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.comment.entity.Comment;
import com.yk.Motivation.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public RsData<Comment> save(Comment comment) {

        commentRepository.save(comment);

        return RsData.of("S-1", "%d 번 댓글이 작성되었습니다.".formatted(comment.getId()), comment);
    }

    public List<Comment> findByArticleId(long id) {
        return commentRepository.findByArticleId(id);
    }

    // 페이징 처리를 위한 메소드
    public Page<Comment> findByArticleIdWithPagination(long articleId, Pageable pageable) {
        return commentRepository.findByArticleId(articleId, pageable);
    }

    // 댓글 삭제
    public void delete(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));


        commentRepository.delete(comment);
    }

    // 댓글 조회 (ID 기반)
    public Comment findById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
    }
/*

    public Comment updateComment(Long commentId, String updatedContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        comment.setContent(updatedContent);
        return commentRepository.save(comment);
    }
*/
    public Comment updateComment(Long commentId, String updatedContent, int updatedRating) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        comment.setContent(updatedContent);
        comment.setRating(updatedRating); // 별점도 업데이트
        return commentRepository.save(comment);
    }
}

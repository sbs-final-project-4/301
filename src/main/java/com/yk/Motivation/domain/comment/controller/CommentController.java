package com.yk.Motivation.domain.comment.controller;

import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.comment.entity.Comment;
import com.yk.Motivation.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usr/comment")
@RequiredArgsConstructor

public class CommentController {

    private final CommentService commentService;
    private final Rq rq;

    @PostMapping("/write")
    public String write(@ModelAttribute Comment comment) {
        RsData<Comment> saveRs = commentService.save(comment);

        return rq.redirectOrBack("/usr/article/free1/detail/%s".formatted(comment.getArticle().getId()), saveRs);
    }

    // 댓글 삭제 처리
    @RequestMapping(value = "/delete/{commentId}", method = RequestMethod.GET)
    public String delete(@PathVariable long commentId) {
        Comment comment = commentService.findById(commentId);
        long articleId = comment.getArticle().getId();
        commentService.delete(commentId);
        return "redirect:/usr/article/free1/detail/" + articleId;
    }

    @PostMapping("/modify")
    public String modify(@RequestParam long commentId, @RequestParam String content, @RequestParam int rating) {
        Comment updatedComment = commentService.updateComment(commentId, content, rating);
        return "redirect:/usr/article/free1/detail/" + updatedComment.getArticle().getId();
    }
}

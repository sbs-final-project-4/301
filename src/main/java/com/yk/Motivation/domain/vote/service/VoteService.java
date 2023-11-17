package com.yk.Motivation.domain.vote.service;

import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.article.repository.ArticleRepository;
import com.yk.Motivation.domain.vote.entity.Vote;
import com.yk.Motivation.domain.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public Vote addVote(Vote vote) {
        Article article = articleRepository.findById(vote.getArticle().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid article ID")); // 예외처리
        article.setVoteCount(article.getVoteCount() + 1);
        return voteRepository.save(vote);
    }

    public boolean hasAlreadyVoted(String nickName, Long articleId) {
        return voteRepository.existsByNickNameAndArticleId(nickName, articleId);
    }

    @Transactional
    public void cancelVote(String nickName, Long articleId) {
        Vote vote = voteRepository.findByNickNameAndArticleId(nickName, articleId)
                .orElseThrow(() -> new IllegalArgumentException("Vote not found"));

        voteRepository.delete(vote);

        // Article의 voteCount 감소
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article ID"));
        article.setVoteCount(article.getVoteCount() - 1);
    }

}

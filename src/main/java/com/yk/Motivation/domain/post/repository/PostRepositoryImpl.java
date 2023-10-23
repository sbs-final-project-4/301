package com.yk.Motivation.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import static com.yk.Motivation.domain.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findByKw(Member author, String kwType, String kw, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.author.eq(author));

        switch (kwType) {
            case "subject" -> builder.and(post.subject.containsIgnoreCase(kw));
            case "body" -> builder.and(post.body.containsIgnoreCase(kw));
            default -> builder.and(
                    post.subject.containsIgnoreCase(kw)
                            .or(post.body.containsIgnoreCase(kw))
            );
        }

        JPAQuery<Post> postsQuery = jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .where(builder);

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(post.getType(), post.getMetadata());
            postsQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        postsQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(postsQuery.fetch(), pageable, totalQuery::fetchOne);
    }
}
package com.server.cogito.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.cogito.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.server.cogito.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findWithSearchConditions(String query, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin()
                .where(containQuery(query))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch();
        JPQLQuery<Post> count = queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin()
                .where(containQuery(query));

        return PageableExecutionUtils.getPage(result,pageable,count::fetchCount);
    }

    @Override
    public Page<Post> findWithoutSearchConditions(Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin()
                .orderBy(post.createdAt.desc())
                .offset(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Post> count = queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin();

        return PageableExecutionUtils.getPage(result,pageable,count::fetchCount);
    }

    private BooleanExpression containQuery(String query){
        return post.title.contains(query).or(post.content.contains(query));
    }


}

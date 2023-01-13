package com.server.cogito.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.cogito.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.server.cogito.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findWithSearchConditions(String query, Pageable pageable) {
        return queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin()
                .orderBy(queryOrder(query).desc(),post.createdAt.desc())
                .offset(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Post> findWithoutSearchConditions(Pageable pageable) {
        return queryFactory.selectFrom(post)
                .innerJoin(post.user).fetchJoin()
                .orderBy(post.createdAt.desc())
                .offset(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression eqQuery(String query){
        return post.title.eq(query).or(post.content.eq(query));
    }

    private BooleanExpression likeQuery(String query){
        return post.title.like(query).or(post.content.like(query));
    }

    private NumberExpression<Integer> queryOrder(String query){
        return new CaseBuilder()
                .when(eqQuery(query)).then(3)
                .when(likeQuery(query)).then(2)
                .otherwise(1);
    }


}

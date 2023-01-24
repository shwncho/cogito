package com.server.cogito.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.cogito.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;


import static com.server.cogito.user.entity.QUser.*;
import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findWithSearchConditions(String query, Pageable pageable) {
        List<User> result = queryFactory.selectFrom(user)
                .where(containQuery(query))
                .orderBy(user.score.desc(), user.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<User> count = queryFactory.selectFrom(user)
                .where(containQuery(query));

        return PageableExecutionUtils.getPage(result,pageable,count::fetchCount);
    }

    @Override
    public Page<User> findWithoutSearchConditions(Pageable pageable) {
        List<User> result = queryFactory.selectFrom(user)
                .orderBy(user.score.desc(), user.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<User> count = queryFactory.selectFrom(user);

        return PageableExecutionUtils.getPage(result,pageable,count::fetchCount);
    }

    private BooleanExpression containQuery(String query){
        return user.nickname.contains(query);
    }
}

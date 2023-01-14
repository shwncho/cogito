package com.server.cogito.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.entity.BaseEntity;
import lombok.RequiredArgsConstructor;

import static com.server.cogito.comment.entity.QComment.*;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findCommentsByPostId(Long postId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .innerJoin(comment.user).fetchJoin()
                .where(comment.post.id.eq(postId), comment.status.eq(BaseEntity.Status.ACTIVE))
                .orderBy(
                        comment.selected.asc(),
                        comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc()
                ).fetch();
    }
}

package com.server.cogito.comment.repository;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndStatus(Long commentId, BaseEntity.Status status);
}

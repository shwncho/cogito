package com.server.cogito.comment.repository;

import com.server.cogito.comment.entity.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findCommentsByPostId(Long postId);
}

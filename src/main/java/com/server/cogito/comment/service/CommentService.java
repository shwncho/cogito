package com.server.cogito.comment.service;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.comment.CommentNotFoundException;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.server.cogito.common.entity.BaseEntity.Status.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createComment(AuthUser authUser, CommentRequest commentRequest){
        commentRepository.save(
                Comment.builder()
                        .post(postRepository.findByIdAndStatus(commentRequest.getPostId(), ACTIVE).orElseThrow(PostNotFoundException::new))
                        .parent(commentRequest.getParentId() != null ?
                                commentRepository.findByIdAndStatus(commentRequest.getParentId(), ACTIVE).orElseThrow(CommentNotFoundException::new)
                                : null)
                        .content(commentRequest.getContent())
                        .user(authUser.getUser())
                        .build()
        );
    }

    @Transactional
    public void deleteComment(AuthUser authUser, Long commentId){
        Comment comment = commentRepository.findByIdAndStatus(commentId,ACTIVE)
                .orElseThrow(CommentNotFoundException::new);

        if(!Objects.equals(authUser.getUserId(), comment.getUser().getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }

        if(comment.getParent()==null){
            comment.getChild().forEach(Comment::deleteComment);
            comment.deleteComment();
        }
        else comment.deleteComment();
    }
}
package com.server.cogito.comment.service;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.dto.request.UpdateCommentRequest;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.exception.comment.CommentErrorCode;
import com.server.cogito.common.exception.comment.CommentInvalidException;
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

import static com.server.cogito.common.entity.BaseEntity.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createComment(AuthUser authUser, CommentRequest commentRequest){
        commentRepository.save(
                Comment.builder()
                        .post(postRepository.findByIdAndStatus(commentRequest.getPostId(), ACTIVE)
                                .orElseThrow(PostNotFoundException::new))
                        .parent(commentRequest.getParentId() != null ?
                                getParent(commentRequest.getParentId()) : null)
                        .content(commentRequest.getContent())
                        .user(authUser.getUser())
                        .build()
        );
        authUser.getUser().addScore(3);
    }

    private Comment getParent(Long parentId){
        Comment parent = commentRepository.findByIdAndStatus(parentId, ACTIVE)
                .orElseThrow(CommentNotFoundException::new);
        validateParent(parent);
        return parent;
    }

    private void validateParent(Comment parent){
        if (parent.getParent() != null) {
            throw new CommentInvalidException(CommentErrorCode.COMMENT_PARENT_INVALID);
        }
    }

    @Transactional
    public void updateComment(AuthUser authUser, Long commentId, UpdateCommentRequest updateCommentRequest){
        Comment comment = commentRepository.findByIdAndStatus(commentId,ACTIVE)
                .orElseThrow(CommentNotFoundException::new);

        validateUserId(authUser, comment);

        comment.changeComment(updateCommentRequest.getContent());
    }

    @Transactional
    public void deleteComment(AuthUser authUser, Long commentId){
        Comment comment = commentRepository.findByIdAndStatus(commentId,ACTIVE)
                .orElseThrow(CommentNotFoundException::new);

        validateUserId(authUser, comment);

        if(comment.getParent()==null){
            comment.getChild().forEach(Comment::deleteComment);
            comment.deleteComment();
        }
        else comment.deleteComment();

        authUser.getUser().subtractScore(3);
    }

    private static void validateUserId(AuthUser authUser, Comment comment) {
        if(!Objects.equals(authUser.getUserId(), comment.getUser().getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }
    }

    @Transactional
    public void likeComment(AuthUser authUser, Long commentId){
        Comment comment = commentRepository.findByIdAndStatus(commentId, ACTIVE)
                .orElseThrow(CommentNotFoundException::new);
        validateParent(comment);
        validateEqualUserId(authUser,comment);

        comment.addLike();

    }

    private static void validateEqualUserId(AuthUser authUser, Comment comment) {
        if(Objects.equals(authUser.getUserId(), comment.getUser().getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }
    }

    @Transactional
    public void dislikeComment(AuthUser authUser, Long commentId){
        Comment comment = commentRepository.findByIdAndStatus(commentId, ACTIVE)
                .orElseThrow(CommentNotFoundException::new);
        validateParent(comment);
        validateEqualUserId(authUser,comment);

        comment.subtractLike();
    }

    @Transactional
    public void selectComment(AuthUser authUser, Long commentId){
        Comment comment = commentRepository.findByIdAndStatus(commentId, ACTIVE)
                .orElseThrow(CommentNotFoundException::new);
        validateParent(comment);
        validateUserId(authUser,comment);

        comment.selectComment();
        comment.getUser().addScore(5);

    }
}

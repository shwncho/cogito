package com.server.cogito.comment.service;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.dto.request.UpdateCommentRequest;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.comment.CommentNotFoundException;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 생성 성공")
    public void createComment_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = createPost(user);
        CommentRequest request = createCommentRequest();
        given(postRepository.findByIdAndStatus(any(),any())).willReturn(Optional.of(post));
        //when
        commentService.createComment(authUser,request);
        //then
        assertAll(
                ()->verify(postRepository).findByIdAndStatus(any(),any()),
                ()->verify(commentRepository).save(any(Comment.class))
        );

    }

    @Test
    @DisplayName("댓글 생성 실패 / 존재하지 않는 게시물")
    public void createComment_fail_not_found_post() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = createPost(user);
        CommentRequest request = createCommentRequest();
        given(postRepository.findByIdAndStatus(any(),any()))
                .willThrow(PostNotFoundException.class);

        //expected
        assertThatThrownBy(()->commentService.createComment(authUser,request))
                .isExactlyInstanceOf(PostNotFoundException.class);
    }


    private User mockUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    private CommentRequest createCommentRequest(){
        return CommentRequest.builder()
                .postId(1L)
                .content("테스트")
                .build();
    }

    private Post createPost(User user){
        return Post.builder()
                .title("테스트")
                .content("테스트")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("댓글 수정 성공")
    public void updateComment_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Comment comment = getComment();
        String originalContent = comment.getContent();
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                        .content("수정 댓글")
                        .build();
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.updateComment(authUser, comment.getId(), request);
        //then
        assertThat(originalContent).isNotEqualTo(request.getContent());
    }

    @Test
    @DisplayName("댓글 수정 실패 / 존재하지 않는 댓글")
    public void updateComment_fail_not_found() throws Exception {
        //given
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->commentService.deleteComment(any(),any()))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void deleteComment_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Comment comment = getComment();
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.deleteComment(authUser,comment.getId());
        //then
        assertThat(comment.getStatus()).isEqualTo(BaseEntity.Status.INACTIVE);
    }
    
    @Test
    @DisplayName("댓글 삭제 실패 / 존재하지 않는 댓글")
    public void deleteComment_fail_not_found() throws Exception {
        //given
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->commentService.deleteComment(any(),any()))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }


    private Comment getComment(){
        return Comment.builder()
                .user(mockUser())
                .content("테스트")
                .post(createPost(mockUser()))
                .build();
    }

}
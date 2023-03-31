package com.server.cogito.comment.service;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.dto.request.UpdateCommentRequest;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.comment.CommentNotFoundException;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.notification.service.NotificationService;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 생성 성공")
    public void create_comment_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = createPost(user);
        CommentRequest request = createCommentRequest();
        given(userRepository.findByEmailAndStatus(any(),any())).willReturn(Optional.of(user));
        given(postRepository.findByIdAndStatus(any(),any())).willReturn(Optional.of(post));

        //when
        commentService.createComment(authUser,request);
        //then
        assertAll(
                ()->verify(userRepository).findByEmailAndStatus(any(),any()),
                ()->verify(postRepository).findByIdAndStatus(any(),any()),
                ()->verify(commentRepository).save(any(Comment.class)),
                ()->verify(notificationService).send(any(),any(),any(),any()),
                ()->assertThat(user.getScore()).isEqualTo(2)
        );

    }

    @Test
    @DisplayName("댓글 생성 실패 / 존재하지 않는 게시물")
    public void create_comment_fail_not_found_post() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = createPost(user);
        CommentRequest request = createCommentRequest();
        given(userRepository.findByEmailAndStatus(any(),any())).willReturn(Optional.of(user));
        given(postRepository.findByIdAndStatus(any(),any()))
                .willThrow(PostNotFoundException.class);

        //expected
        assertThatThrownBy(()->commentService.createComment(authUser,request))
                .isExactlyInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 생성 실패 / 존재하지 않는 유저")
    public void create_comment_fail_not_found_user() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        CommentRequest request = createCommentRequest();
        given(userRepository.findByEmailAndStatus(any(),any()))
                .willThrow(new UserNotFoundException());
        //expected
        assertThatThrownBy(()->commentService.createComment(authUser,request))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }


    private User mockUser(){
        return User.builder()
                .id(1L)
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    private User githubUser(){
        return User.builder()
                .id(2L)
                .email("github@github.com")
                .nickname("github")
                .provider(Provider.GITHUB)
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
    public void update_comment_success() throws Exception {
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
    public void update_comment_fail_not_found() throws Exception {
        //given
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->commentService.deleteComment(any(),any()))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void delete_comment_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Comment comment = createComment(user);
        user.addScore(1);
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.deleteComment(authUser,comment.getId());
        //then
        assertAll(
                ()->assertThat(comment.getStatus()).isEqualTo(BaseEntity.Status.INACTIVE),
                ()->assertThat(user.getScore()).isEqualTo(1)

        );
    }
    
    @Test
    @DisplayName("댓글 삭제 실패 / 존재하지 않는 댓글")
    public void delete_comment_fail_not_found() throws Exception {
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

    @Test
    @DisplayName("댓글 좋아요 성공")
    public void like_comment_success() throws Exception {
        //given
        User githubUser = githubUser();
        AuthUser authUser = AuthUser.of(githubUser);
        Comment comment = getComment();
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.likeComment(authUser,comment.getId());
        //then
        assertThat(comment.getLikeCnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 좋아요 실패 / 존재하지 않는 댓글")
    public void like_comment_fail_not_found() throws Exception {
        //given
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->commentService.likeComment(any(),any()))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 싫어요 성공")
    public void dislike_comment() throws Exception {
        //given
        User githubUser = githubUser();
        AuthUser authUser = AuthUser.of(githubUser);
        Comment comment = getComment();
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.dislikeComment(authUser,comment.getId());
        //then
        assertThat(comment.getLikeCnt()).isEqualTo(-1);
    }

    @Test
    @DisplayName("댓글 싫어요 실패 / 존재하지 않는 댓글")
    public void dislike_comment_fail_not_found() throws Exception {
        //given
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->commentService.dislikeComment(any(),any()))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 채택 성공")
    public void select_comment_success() throws Exception {
        //given
        User user = mockUser();
        User githubUser = githubUser();
        AuthUser authUser = AuthUser.of(githubUser);
        Comment comment = createComment(user);
        given(commentRepository.findByIdAndStatus(comment.getId(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(comment));
        //when
        commentService.selectComment(authUser,comment.getId());
        //then
        assertAll(
                ()->assertThat(comment.getSelected()).isEqualTo(1),
                ()->assertThat(user.getScore()).isEqualTo(6)
        );
    }

    private Comment createComment(User user){
        return Comment.builder()
                .user(user)
                .content("테스트")
                .post(createPost(mockUser()))
                .build();
    }

    @Test
    @DisplayName("댓글 채택 실패 / 본인 댓글을 채택할 경우")
    public void select_comment_fail_invalid_user() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Comment comment = createComment(user);
        given(commentRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(comment));
        //expected
        assertThatThrownBy(()->commentService.selectComment(authUser, comment.getId()))
                .isExactlyInstanceOf(UserInvalidException.class);
    }

}
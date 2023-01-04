package com.server.cogito.comment.service;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

}
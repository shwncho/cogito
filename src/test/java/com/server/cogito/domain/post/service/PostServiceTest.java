package com.server.cogito.domain.post.service;

import com.server.cogito.domain.file.entity.PostFile;
import com.server.cogito.domain.like.entity.Likes;
import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.domain.post.repository.PostRepository;
import com.server.cogito.domain.tag.entity.Tag;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.enums.Provider;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.entity.BaseEntity;
import com.server.cogito.global.common.security.AuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시물 생성 성공")
    void createPost_success() throws Exception{

        //given
        CreatePostRequest request = createPostRequest();
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        given(userRepository.findByEmailAndStatus(authUser.getUsername(), BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(user));
        Post post = Post.of(request.getTitle(),request.getContent(),user);
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        Long postId = postService.createPost(authUser,request);

        //then
        assertAll(
                ()->verify(userRepository).findByEmailAndStatus(authUser.getUsername(), BaseEntity.Status.ACTIVE),
                ()->verify(postRepository).save(any(Post.class))
        );
    }

    private static CreatePostRequest createPostRequest() {
        CreatePostRequest request = CreatePostRequest.builder()
                .title("테스트")
                .content("테스트")
                .build();
        request.setFiles(List.of("file1"));
        request.setTags(List.of("tag1"));
        return request;
    }

    private User mockUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }
}
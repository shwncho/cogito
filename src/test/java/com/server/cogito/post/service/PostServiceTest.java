package com.server.cogito.post.service;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostPageResponse;
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
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

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
        PostRequest request = createPostRequest();
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of(request.getTitle(),request.getContent(),user);
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        CreatePostResponse response = postService.createPost(authUser,request);

        //then
        assertAll(
                ()->verify(postRepository).save(any(Post.class)),
                ()->assertEquals(3,user.getScore())
        );
    }

    private static PostRequest createPostRequest() {
        PostRequest request = PostRequest.builder()
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

    @Test
    @DisplayName("게시물 조회 성공 / 최신순")
    void getPosts_success_latest() throws Exception {

        //given
        User user = mockUser();
        Pageable pageable = PageRequest.of(1,10,Sort.by("createdAt").descending());
        List<Post> posts = List.of(Post.of("테스트 제목1","테스트 본문1", user),
                Post.of("테스트 제목2","테스트 본문2", user));
        Page<Post> postPage = new PageImpl<>(posts);
        given(postRepository.findAll(any(PageRequest.class))).willReturn(postPage);

        //when
        PostPageResponse response = postService.getPosts(pageable);

        assertAll(
                ()->verify(postRepository).findAll(any(PageRequest.class)),
                ()->assertThat(2).isEqualTo(response.getPosts().size())
        );

    }
}
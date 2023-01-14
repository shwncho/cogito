package com.server.cogito.post.service;

import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.file.repository.PostFileRepository;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.request.UpdatePostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.dto.response.PostResponse;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.tag.repository.TagRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostFileRepository postFileRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시물 생성 성공")
    void create_post_success() throws Exception{

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
                ()->assertThat(user.getScore()).isEqualTo(3)
        );
    }

    private static PostRequest createPostRequest() {
        return PostRequest.builder()
                .title("테스트")
                .content("테스트")
                .files(List.of("file1"))
                .tags(List.of("tag1"))
                .build();
    }

    private User mockUser(){
        return User.builder()
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

    @Test
    @DisplayName("게시물 조회 성공 / 검색 조건 없을 경우")
    void get_posts_success_latest() throws Exception {

        //given
        User user = mockUser();
        Pageable pageable = PageRequest.of(0,15,Sort.by("createdAt").descending());
        List<Post> posts = List.of(Post.of("테스트 제목1","테스트 본문1", user),
                Post.of("테스트 제목2","테스트 본문2", user));
        given(postRepository.findWithoutSearchConditions(pageable)).willReturn(posts);

        //when
        PostPageResponse response = postService.getPosts("",pageable);

        assertAll(
                ()->verify(postRepository).findWithoutSearchConditions(any(Pageable.class)),
                ()->assertThat(2).isEqualTo(response.getPosts().size())
        );

    }

    @Test
    @DisplayName("게시물 조회 성공 / 검색 조건 있을 경우")
    void get_posts_success_query() throws Exception {

        //given
        User user = mockUser();
        Pageable pageable = PageRequest.of(0,15,Sort.by("createdAt").descending());
        List<Post> posts = List.of(Post.of("테스트 제목1","테스트 본문1", user));
        given(postRepository.findWithSearchConditions(any(),any())).willReturn(posts);

        //when
        PostPageResponse response = postService.getPosts("테스트 제목1",pageable);

        assertAll(
                ()->verify(postRepository).findWithSearchConditions(anyString(),any(Pageable.class)),
                ()->assertThat(1).isEqualTo(response.getPosts().size())
        );

    }

    @Test
    @DisplayName("게시물 단건 조회 성공")
    public void get_post_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of("테스트 제목","테스트 본문",user);
        given(postRepository.findPostByIdAndStatus(1L, BaseEntity.Status.ACTIVE))
                .willReturn(Optional.of(post));
        //when
        PostResponse response = postService.getPost(authUser,1L);
        //then
        verify(commentRepository).findCommentsByPostId(any());
    }

    @Test
    @DisplayName("게시물 단건 조회 실패 / 존재하지 않는 게시물")
    public void get_post_fail_not_found() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        given(postRepository.findPostByIdAndStatus(1L, BaseEntity.Status.ACTIVE))
                .willReturn(Optional.empty());

        //expected
        assertThatThrownBy(()->postService.getPost(authUser,1L))
                .isExactlyInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("게시물 수정 성공")
    public void update_post_success() throws Exception {
        //given
        Post post = Post.of("테스트","테스트",mockUser());
        String originalTitle = post.getTitle();
        String originalContent = post.getContent();
        UpdatePostRequest request = createUpdatePostRequest();
        given(postRepository.findPostByIdAndStatus(any(),any()))
                .willReturn(Optional.of(post));
        //when
        postService.updatePost(1L,request);
        //then
        assertAll(
                ()->assertThat(originalTitle).isNotEqualTo(request.getTitle()),
                ()->assertThat(originalContent).isNotEqualTo(request.getContent())
        );
    }

    private UpdatePostRequest createUpdatePostRequest(){
        return UpdatePostRequest.builder()
                .title("수정 제목")
                .content("수정 본문")
                .files(List.of("수정 파일1"))
                .tags(List.of("수정 태그1"))
                .build();
    }

    @Test
    @DisplayName("게시물 수정 실패 / 존재하지 않는 게시물")
    public void update_post_fail_not_found() throws Exception {
        //given
        UpdatePostRequest request = createUpdatePostRequest();
        given(postRepository.findPostByIdAndStatus(1L, BaseEntity.Status.ACTIVE))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->postService.updatePost(1L,request))
                .isExactlyInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("게시물 삭제 성공")
    public void delete_post_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of("테스트 제목","테스트 본문",user);
        given(postRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(post));
        //when
        postService.deletePost(authUser,post.getId());
        //then
        assertAll(
                ()->assertThat(user.getScore()).isEqualTo(-1),
                ()->assertThat(post.getStatus()).isEqualTo(BaseEntity.Status.INACTIVE)
        );
    }

    @Test
    @DisplayName("게시물 삭제 실패 / 존재하지 않는 게시물")
    public void delete_post_fail_not_found() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of("테스트 제목","테스트 본문",user);
        given(postRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->postService.deletePost(authUser,post.getId()))
                .isExactlyInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("게시물 삭제 실패 / 유효하지 않은 유저")
    public void delete_post_fail_invalid_user() throws Exception {
        //given
        User user = mockUser();
        User githubUser = githubUser();
        AuthUser authUser = AuthUser.of(githubUser);
        Post post = Post.of("테스트 제목","테스트 본문",user);
        given(postRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(post));
        //expected
        assertThatThrownBy(()->postService.deletePost(authUser,post.getId()))
                .isExactlyInstanceOf(UserInvalidException.class);
    }
}
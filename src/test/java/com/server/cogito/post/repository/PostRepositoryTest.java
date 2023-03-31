package com.server.cogito.post.repository;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.service.PostService;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PostRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Test
    @DisplayName("게시물 좋아요")
    public void like_post() throws Exception {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //given
        User user = mockUser();
        User githubUser = githubUser();
        AuthUser authUser = AuthUser.of(githubUser);
        Post post = createPost("테스트 제목","테스트 내용",user);
        userRepository.save(user);
        userRepository.save(githubUser);
        postRepository.save(post);

        //when

        for(int i=0; i<threadCount; i++){
            executorService.submit(()->{
                try{
                    postService.likePost(authUser,1L);
                }
                finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //then
        Post result = postRepository.findByIdAndStatus(1L, BaseEntity.Status.ACTIVE)
                        .get();
        assertThat(result.getLikeCnt()).isEqualTo(threadCount);
    }

    private static Post createPost(String title, String content, User user){
        return Post.builder()
                .id(1L)
                .title(title)
                .content(content)
                .user(user)
                .build();
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
}

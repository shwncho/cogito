package com.server.cogito.notification.service;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.exception.notification.NotificationNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.notification.dto.NotificationResponses;
import com.server.cogito.notification.entity.Notification;
import com.server.cogito.notification.repository.EmitterRepository;
import com.server.cogito.notification.repository.NotificationRepository;
import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    EmitterRepository emitterRepository;

    @Mock
    NotificationRepository notificationRepository;

    @InjectMocks
    NotificationService notificationService;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Test
    @DisplayName("sse 연결 성공")
    public void get_subscribe_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        given(emitterRepository.save(any(),any()))
                .willReturn(new SseEmitter(DEFAULT_TIMEOUT));
        //expected
        assertDoesNotThrow(()->notificationService.subscribe(authUser,""));
    }

    private User mockUser(){
        return User.builder()
                .id(1L)
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    @Test
    @DisplayName("알림 조회")
    public void get_notifications_success() throws Exception {
        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        given(notificationRepository.findAllByReceiverId(any()))
                .willReturn(List.of(createNotification(user)));
        //when
        NotificationResponses responses = notificationService.getNotifications(authUser);
        //then
        assertAll(
                ()->assertThat(responses.getUnreadCount()).isEqualTo(1),
                ()->assertThat(responses.getNotificationResponses().size()).isEqualTo(1)
        );
    }

    private Notification createNotification(User receiver) {
        Post post = createPost(receiver);
        User githubUser = githubUser();
        Comment comment = createComment(post,githubUser);
        return Notification.builder()
                .receiver(receiver)
                .content("새로운 댓글이 달렸습니다.")
                .post(post)
                .comment(comment)
                .url("/questions/1")
                .isRead(false)
                .build();
    }

    private Post createPost(User receiver){
        return Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(receiver)
                .build();
    }
    private Comment createComment(Post post, User user){
        return Comment.builder()
                .post(post)
                .content("테스트")
                .user(user).build();
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
    @DisplayName("알림 확인 성공")
    public void read_notification_success() throws Exception {
        //given
        User user = mockUser();
        Notification notification = createNotification(user);
        given(notificationRepository.findById(any()))
                .willReturn(Optional.of(notification));
        //when
        notificationService.readNotification(1L);
        //then
        assertThat(notification.isRead()).isEqualTo(true);
    }

    @Test
    @DisplayName("알림 확인 실패 / 존재하지 않는 알림")
    public void read_notification_fail_not_found() throws Exception {
        //given
        given(notificationRepository.findById(any()))
                .willReturn(Optional.empty());
        //expected
        assertThatThrownBy(()->notificationService.readNotification(any()))
                .isExactlyInstanceOf(NotificationNotFoundException.class);
    }
}
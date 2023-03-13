package com.server.cogito.notification.service;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.notification.repository.EmitterRepository;
import com.server.cogito.notification.repository.NotificationRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

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
}
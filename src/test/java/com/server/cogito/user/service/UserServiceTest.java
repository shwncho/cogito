package com.server.cogito.user.service;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저 프로필 조회")
    public void getMe_success() throws Exception {
        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);

        //when
        UserResponse response = userService.getMe(authUser);

        //then
        assertAll(
                ()->assertEquals(user.getNickname(),response.getNickname()),
                ()->assertEquals(user.getProfileImgUrl(),response.getProfileImgUrl()),
                ()->assertEquals(user.getScore(),response.getScore()),
                ()->assertEquals(user.getIntroduce(),response.getIntroduce())
        );
    }

    private User mockKakaoUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }
}
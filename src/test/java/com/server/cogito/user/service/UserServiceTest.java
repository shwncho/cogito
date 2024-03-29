package com.server.cogito.user.service;

import com.server.cogito.common.exception.user.UserNicknameExistException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.dto.response.UserPageResponse;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저 랭킹 조회 성공")
    public void get_users_success() throws Exception {
        //given
        User user = mockKakaoUser();
        Pageable pageable = PageRequest.of(0,15);
        Page<User> users = new PageImpl<>(List.of(user));
        given(userRepository.findWithSearchConditions(any(),any()))
                .willReturn(users);
        //when
        UserPageResponse response = userService.getUsers("kakao",pageable);
        //then
        assertThat(response.getUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("본인 프로필 조회 성공")
    public void get_me_success() throws Exception {
        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);

        //when
        UserResponse response = userService.getMe(authUser);

        //then
        assertAll(
                ()->assertThat(user.getId()).isEqualTo(response.getUserId()),
                ()->assertThat(user.getNickname()).isEqualTo(response.getNickname()),
                ()->assertThat(user.getProfileImgUrl()).isEqualTo(response.getProfileImgUrl()),
                ()->assertThat(user.getScore()).isEqualTo(response.getScore()),
                ()->assertThat(user.getIntroduce()).isEqualTo(response.getIntroduce())
        );
    }

    @Test
    @DisplayName("본인 프로필 조회 성공 / 비로그인 유저일 경우")
    public void get_me_success_no_auth() throws Exception {
        //given, when
        UserResponse response = userService.getMe(null);

        //then
        assertAll(
                ()->assertThat(response.getUserId()).isNull(),
                ()->assertThat(response.getNickname()).isNull(),
                ()->assertThat(response.getProfileImgUrl()).isNull(),
                ()->assertThat(response.getScore()).isEqualTo(0),
                ()->assertThat(response.getIntroduce()).isNull()
        );
    }

    @Test
    @DisplayName("유저 프로필 조회 성공")
    public void get_user_success() throws Exception {
        //given
        User user = mockKakaoUser();
        given(userRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(user));

        //when
        UserResponse response = userService.getUser(user.getId());

        //then
        assertAll(
                ()->assertThat(user.getId()).isEqualTo(response.getUserId()),
                ()->assertThat(user.getNickname()).isEqualTo(response.getNickname()),
                ()->assertThat(user.getProfileImgUrl()).isEqualTo(response.getProfileImgUrl()),
                ()->assertThat(user.getScore()).isEqualTo(response.getScore()),
                ()->assertThat(user.getIntroduce()).isEqualTo(response.getIntroduce())
        );
    }

    private User mockKakaoUser(){
        return User.builder()
                .id(1L)
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    @Test
    @DisplayName("유저 프로필 수정 성공")
    public void update_user_success() throws Exception {
        //given
        User user = mockKakaoUser();
        String originNickname = user.getNickname();
        String originProfileImgUrl = user.getProfileImgUrl();
        String originIntroduce = user.getIntroduce();

        AuthUser authUser = AuthUser.of(user);
        UserRequest request = UserRequest.builder()
                .nickname("수정")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        given(userRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(user));
        //when
        userService.updateUser(authUser, user.getId(),request);

        //then
        assertAll(
                ()->assertThat(originNickname).isNotEqualTo(request.getNickname()),
                ()->assertThat(originProfileImgUrl).isNotEqualTo(request.getProfileImgUrl()),
                ()->assertThat(originIntroduce).isNotEqualTo(request.getIntroduce())
        );
    }

    @Test
    @DisplayName("유저 프로필 수정 실패")
    public void update_user_fail_existNickname() throws Exception {
        //given
        User user = mockKakaoUser();
        String originNickname = user.getNickname();
        String originProfileImgUrl = user.getProfileImgUrl();
        String originIntroduce = user.getIntroduce();

        AuthUser authUser = AuthUser.of(user);
        UserRequest request = UserRequest.builder()
                .nickname("카카오")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        given(userRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(user));
        given(userRepository.existsByNickname(request.getNickname())).willReturn(true);

        //expected
        assertThatThrownBy(()->userService.updateUser(authUser,user.getId(),request))
                .isExactlyInstanceOf(UserNicknameExistException.class);
    }
}
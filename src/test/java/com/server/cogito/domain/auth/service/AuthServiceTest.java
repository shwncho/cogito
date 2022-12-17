package com.server.cogito.domain.auth.service;

import com.server.cogito.domain.auth.dto.TokenResponse;
import com.server.cogito.domain.auth.dto.response.KaKaoUser;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.enums.Provider;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.security.AuthUser;
import com.server.cogito.global.common.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final String ACCESS_TOKEN = "testAccessToken";
    private final String REFRESH_TOKEN = "testRefreshToken";
    private final String RENEWAL_ACCESS_TOKEN = "refreshedTestAccessToken";
    private final String RENEWAL_REFRESH_TOKEN = "refreshedTestRefreshToken";

    @Mock
    UserRepository userRepository;
    @Mock
    JwtProvider jwtProvider;
    @Mock
    RedisTemplate redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    AuthService authService;

//    @Test
//    @DisplayName("로그인 성공 / 회원가입이 되지 않은 유저일 경우")
//    void signIn_success_notExistUser() throws Exception{
//
//        //given
//        KaKaoUser oauthUser = createKaKaoUser();
//        when(userRepository.findByEmailAndStatus(oauthUser.getEmail(), Status.ACTIVE)
//                .orElse(userRepository.save(User.builder().email("kakao@kakao.com").nickname("kakao").provider(Provider.KAKAO).build())))
//                .thenReturn(mockUser());
//
//        //when
//        User user = mockUser();
//        user.addScore();
//        when(jwtProvider.createToken(any())).thenReturn(mockJwtProvider());
//
//
//        //then
//        assertAll(
//                ()->verify(userRepository).save(any(User.class)),
//                ()->verify(jwtProvider).createToken(any(AuthUser.class))
//        );
//    }

    private KaKaoUser createKaKaoUser(){
        return  KaKaoUser.of("kakao@kakao.com","kakao");
    }

    private User mockUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    private TokenResponse mockJwtProvider(){
        return TokenResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    @Test
    @DisplayName("로그아웃 성공 / refreshToken이 존재할경우")
    void signOut_success_existRefreshToken() throws Exception{

        //given
        User user = mockUser();
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("RT:"+user.getEmail())).willReturn(tokenResponse.getRefreshToken());


        //when
        authService.signOut(AuthUser.of(user),ACCESS_TOKEN);

        //then
        verify(redisTemplate).delete("RT:"+user.getEmail());
    }

    @Test
    @DisplayName("로그아웃 성공 / refreshToken이 존재하지 않을경우")
    void signOut_success_notExistRefreshToken() throws Exception{

        //given
        User user = mockUser();
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(any())).willReturn(null);

        //when
        authService.signOut(AuthUser.of(user),ACCESS_TOKEN);

        //then
        assertThat(valueOperations.get(any())).isEqualTo(null);
        assertThat(tokenResponse.getAccessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() throws Exception{

        //given
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("RT:"+user.getEmail())).willReturn(tokenResponse.getRefreshToken());
        given(jwtProvider.createToken(any())).willReturn(renewalJwtProvider());
        given(jwtProvider.validateToken(any())).willReturn(true);


        //when
        TokenResponse response = authService.reissue(authUser,REFRESH_TOKEN);

        //then
        assertNotEquals(REFRESH_TOKEN, response.getRefreshToken());



    }

    private TokenResponse renewalJwtProvider(){
        return TokenResponse.builder()
                .accessToken(RENEWAL_ACCESS_TOKEN)
                .refreshToken(RENEWAL_REFRESH_TOKEN)
                .build();
    }

}
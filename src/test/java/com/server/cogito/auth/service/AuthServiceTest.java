package com.server.cogito.auth.service;

import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.common.security.jwt.JwtProvider;
import com.server.cogito.infrastructure.oauth.GithubRequester;
import com.server.cogito.infrastructure.oauth.KaKaoRequester;
import com.server.cogito.infrastructure.oauth.OauthHandler;
import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;


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
    KaKaoRequester kakaoRequester;
    @Mock
    GithubRequester githubRequester;
    @Mock
    OauthHandler oauthHandler;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("로그인 성공 / 처음 가입 유저 kakao")
    void login_success_notFoundUser_kakao() throws Exception{

        //given
        String provider = "kakao";
        String code = "code";
        OauthUserInfo oauthUserInfo = mock(OauthUserInfo.class);

        given(oauthHandler.getUserInfoFromCode(Provider.toEnum(provider),code))
                .willReturn(oauthUserInfo);
        given(userRepository.findByEmailAndStatus("kakao@kakao.com", BaseEntity.Status.ACTIVE)
                .orElseGet(()->userRepository.save(any())))
                .willReturn(mockKakaoUser());
        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        //when
        TokenResponse response = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).save(any(User.class)),
                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
                ()->verify(valueOperations).set("RT:"+"kakao@kakao.com",REFRESH_TOKEN,0, MILLISECONDS)

        );
    }

    @Test
    @DisplayName("로그인 성공 / 처음 가입 유저 github")
    void login_success_notFoundUser_github() throws Exception{

        //given
        String provider = "github";
        String code = "code";
        OauthUserInfo oauthUserInfo = mock(OauthUserInfo.class);

        given(oauthHandler.getUserInfoFromCode(Provider.toEnum(provider),code))
                .willReturn(oauthUserInfo);
        given(userRepository.findByEmailAndStatus("github@github.com", BaseEntity.Status.ACTIVE)
                .orElseGet(()->userRepository.save(any())))
                .willReturn(mockGithubUser());
        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        //when
        TokenResponse response = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).save(any(User.class)),
                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
                ()->verify(valueOperations).set("RT:"+"github@github.com",REFRESH_TOKEN,0, MILLISECONDS)

        );
    }

    @Test
    @DisplayName("로그인 성공 / 가입되어있는 유저 kakao")
    void login_success_kakao() throws Exception{

        //given
        String provider = "kakao";
        String code = "code";
        OauthUserInfo oauthUserInfo = mock(OauthUserInfo.class);

        given(oauthHandler.getUserInfoFromCode(Provider.toEnum(provider),code))
                .willReturn(oauthUserInfo);
        given(userRepository.findByEmailAndStatus(any(), any()))
                .willReturn(Optional.of(mockKakaoUser()));
        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        //when
        TokenResponse response = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).findByEmailAndStatus(any(), any()),
                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
                ()->verify(valueOperations).set("RT:"+"kakao@kakao.com",REFRESH_TOKEN,0, MILLISECONDS)

        );
    }

    @Test
    @DisplayName("로그인 성공 / 가입되어있는 유저 github")
    void login_success_github() throws Exception{

        //given
        String provider = "github";
        String code = "code";
        OauthUserInfo oauthUserInfo = mock(OauthUserInfo.class);

        given(oauthHandler.getUserInfoFromCode(Provider.toEnum(provider),code))
                .willReturn(oauthUserInfo);
        given(userRepository.findByEmailAndStatus(any(), any()))
                .willReturn(Optional.of(mockGithubUser()));
        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        //when
        TokenResponse response = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).findByEmailAndStatus(any(), any()),
                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
                ()->verify(valueOperations).set("RT:"+"github@github.com",REFRESH_TOKEN,0, MILLISECONDS)

        );
    }




    private User mockKakaoUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    private User mockGithubUser(){
        return User.builder()
                .email("github@github.com")
                .nickname("github")
                .provider(Provider.GITHUB)
                .build();
    }

    private TokenResponse mockJwtProvider(){
        return TokenResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    @Test
    @DisplayName("로그인 실패 / 제공하지 않는 oauth provider")
    void login_fail_no_oauth_provider() throws Exception{

        //given
        String provider = "naver";
        String code = "code";

        //expected
        assertThatThrownBy(()->authService.login(provider,code))
                .isExactlyInstanceOf(UnsupportedOauthProviderException.class);
    }

    @Test
    @DisplayName("로그아웃 성공 / refreshToken이 존재할경우")
    void logout_success_existRefreshToken() throws Exception{

        //given
        User user = mockKakaoUser();
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("RT:"+user.getEmail())).willReturn(tokenResponse.getRefreshToken());
        given(jwtProvider.getExpiration(any())).willReturn(1L);

        //when
        authService.logout(AuthUser.of(user),ACCESS_TOKEN);

        //then
        assertAll(
                ()->verify(redisTemplate).delete("RT:"+user.getEmail()),
                ()->verify(valueOperations).set(ACCESS_TOKEN,"logout",1L,MILLISECONDS)
        );
    }

    @Test
    @DisplayName("로그아웃 성공 / refreshToken이 존재하지 않을경우")
    void logout_success_notExistRefreshToken() throws Exception{

        //given
        User user = mockKakaoUser();
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(jwtProvider.getExpiration(any())).willReturn(1L);

        //when
        authService.logout(AuthUser.of(user),ACCESS_TOKEN);

        //then
        verify(valueOperations).set(ACCESS_TOKEN,"logout",1L,MILLISECONDS);

    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() throws Exception{

        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);
        TokenResponse tokenResponse = mockJwtProvider();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("RT:"+user.getEmail())).willReturn(tokenResponse.getRefreshToken());
        given(jwtProvider.createToken(any())).willReturn(renewalJwtProvider());
        given(jwtProvider.validateToken(any())).willReturn(true);


        //when
        TokenResponse response = authService.reissue(authUser,REFRESH_TOKEN);

        //then
        assertThat(REFRESH_TOKEN).isNotEqualTo(response.getRefreshToken());



    }

    private TokenResponse renewalJwtProvider(){
        return TokenResponse.builder()
                .accessToken(RENEWAL_ACCESS_TOKEN)
                .refreshToken(RENEWAL_REFRESH_TOKEN)
                .build();
    }

}
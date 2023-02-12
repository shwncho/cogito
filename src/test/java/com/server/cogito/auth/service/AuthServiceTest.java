package com.server.cogito.auth.service;

import com.server.cogito.auth.domain.LogoutAccessToken;
import com.server.cogito.auth.domain.LogoutRefreshToken;
import com.server.cogito.auth.domain.RefreshToken;
import com.server.cogito.auth.dto.response.ReissueTokenResponse;
import com.server.cogito.auth.dto.result.LoginResult;
import com.server.cogito.auth.repository.TokenRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.auth.RefreshTokenInvalidException;
import com.server.cogito.common.exception.auth.RefreshTokenNotFoundException;
import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.common.security.jwt.JwtProvider;
import com.server.cogito.infrastructure.oauth.GithubRequester;
import com.server.cogito.infrastructure.oauth.KaKaoRequester;
import com.server.cogito.infrastructure.oauth.OauthHandler;
import com.server.cogito.notification.repository.EmitterRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


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
    TokenRepository tokenRepository;
    @Mock
    KaKaoRequester kakaoRequester;
    @Mock
    GithubRequester githubRequester;
    @Mock
    OauthHandler oauthHandler;
    @Mock
    EmitterRepository emitterRepository;

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
        given(jwtProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        //when
        LoginResult result = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).save(any(User.class)),
                ()->verify(tokenRepository).saveRefreshToken(any(RefreshToken.class))
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
                .willReturn(mockKakaoUser());
        given(jwtProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        //when
        LoginResult result = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).save(any(User.class)),
                ()->verify(tokenRepository).saveRefreshToken(any(RefreshToken.class))
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
        given(jwtProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        //when
        LoginResult result = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).findByEmailAndStatus(any(), any()),
                ()->verify(tokenRepository).saveRefreshToken(any(RefreshToken.class))
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
        given(jwtProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        //when
        LoginResult result = authService.login(provider,code);

        //then
        assertAll(
                ()->verify(oauthHandler).getUserInfoFromCode(Provider.toEnum(provider),code),
                ()->verify(userRepository).findByEmailAndStatus(any(), any()),
                ()->verify(tokenRepository).saveRefreshToken(any(RefreshToken.class))
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

    private User mockGithubUser(){
        return User.builder()
                .id(1L)
                .email("github@github.com")
                .nickname("github")
                .provider(Provider.GITHUB)
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
    @DisplayName("로그아웃 성공")
    void logout_success_existRefreshToken() throws Exception{

        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);

        //when
        authService.logout(authUser,ACCESS_TOKEN,REFRESH_TOKEN);

        //then
        assertAll(
                ()->verify(jwtProvider).getRemainingMilliSecondsFromToken(ACCESS_TOKEN),
                ()->verify(jwtProvider).getRemainingMilliSecondsFromToken(REFRESH_TOKEN),
                ()->verify(tokenRepository).saveLogoutAccessToken(any(LogoutAccessToken.class)),
                ()->verify(tokenRepository).saveLogoutRefreshToken(any(LogoutRefreshToken.class)),
                ()->verify(emitterRepository).deleteAllStartWithId(any()),
                ()->verify(emitterRepository).deleteAllEventCacheStartWithId(any())

        );
    }


    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() throws Exception{

        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);
        RefreshToken refreshToken = RefreshToken.of(authUser.getUsername(),REFRESH_TOKEN, 1000*60*60*4L);
        given(tokenRepository.findRefreshTokenByUsername(any()))
                .willReturn(Optional.of(refreshToken));
        given(tokenRepository.existsLogoutRefreshTokenById(any()))
                .willReturn(false);

        //when
        ReissueTokenResponse reissue = authService.reissue(authUser,REFRESH_TOKEN);

        //then
        assertAll(
                ()->verify(tokenRepository).deleteRefreshToken(any(RefreshToken.class)),
                ()->verify(jwtProvider).createAccessToken(any(AuthUser.class)),
                ()->verify(jwtProvider).createRefreshToken(any(AuthUser.class)),
                ()->verify(tokenRepository).saveRefreshToken(any(RefreshToken.class))
        );

    }

    @Test
    @DisplayName("토큰 재발급 실패 / 유효하지 않은 refreshToken")
    public void reissue_fail_invalid_refreshToken() throws Exception {
        //given
        User user = mockKakaoUser();
        AuthUser authUser = AuthUser.of(user);
        RefreshToken refreshToken = RefreshToken.of(authUser.getUsername(),REFRESH_TOKEN, 1000*60*60*4L);
        given(tokenRepository.findRefreshTokenByUsername(authUser.getUsername()))
                .willReturn(Optional.of(refreshToken));
        willThrow(new RefreshTokenInvalidException()).given(tokenRepository)
                        .existsLogoutRefreshTokenById(any());
        //expected
        assertThatThrownBy(() -> authService.reissue(authUser,REFRESH_TOKEN))
                .isExactlyInstanceOf(RefreshTokenInvalidException.class);
    }

}
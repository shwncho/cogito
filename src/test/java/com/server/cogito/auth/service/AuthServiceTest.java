package com.server.cogito.auth.service;

import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.infrastructure.oauth.KaKaoRequester;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.common.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    RedisTemplate redisTemplate;
    @Mock
    KaKaoRequester kaKaoRequester;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    AuthService authService;

//    @Test
//    @DisplayName("로그인 성공 / 회원가입이 되지 않은 유저일 경우")
//    void login_success_notExistUser() throws Exception{
//
//        //given
//        SignInRequest request = SignInRequest.builder()
//                .accessToken("oauthToken")
//                .provider("KAKAO")
//                .build();
//        KaKaoUser oauthUser = createKaKaoUser();
//        given(kaKaoRequester.getKaKaoUser(any())).willReturn(oauthUser);
//        given(userRepository.findByEmailAndStatus(oauthUser.getEmail(), BaseEntity.Status.ACTIVE)
//                .orElseGet(()->userRepository.save(any())))
//                .willReturn(mockUser());
//        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
//        given(redisTemplate.opsForValue()).willReturn(valueOperations);
//
//        //when
//        TokenResponse response = authService.login(request);
//
//        //then
//        assertAll(
//                ()->verify(userRepository).save(any(User.class)),
//                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
//                ()->verify(valueOperations).set("RT:"+oauthUser.getEmail(),REFRESH_TOKEN,0, MILLISECONDS)
//
//        );
//    }
//
//    @Test
//    @DisplayName("로그인 성공 / 회원가입이 되어있는 유저일 경우")
//    void login_success_existUser() throws Exception{
//        //given
//        SignInRequest request = SignInRequest.builder()
//                .accessToken("oauthToken")
//                .provider("KAKAO")
//                .build();
//        KaKaoUser oauthUser = createKaKaoUser();
//        given(kaKaoRequester.getKaKaoUser(any())).willReturn(oauthUser);
//        given(userRepository.findByEmailAndStatus(oauthUser.getEmail(), BaseEntity.Status.ACTIVE))
//                .willReturn(Optional.of(mockUser()));
//        given(jwtProvider.createToken(any())).willReturn(mockJwtProvider());
//        given(redisTemplate.opsForValue()).willReturn(valueOperations);
//
//        //when
//        TokenResponse response = authService.login(request);
//
//        //then
//        assertAll(
//                ()->verify(userRepository).findByEmailAndStatus(oauthUser.getEmail(), BaseEntity.Status.ACTIVE),
//                ()->verify(jwtProvider).createToken(any(AuthUser.class)),
//                ()->verify(valueOperations).set("RT:"+oauthUser.getEmail(),REFRESH_TOKEN,0, MILLISECONDS)
//
//        );
//    }

//    private KaKaoUser createKaKaoUser(){
//        return  KaKaoUser.of("kakao@kakao.com","kakao");
//    }

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
    void logout_success_existRefreshToken() throws Exception{

        //given
        User user = mockUser();
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
        User user = mockUser();
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
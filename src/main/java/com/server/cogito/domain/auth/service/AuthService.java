package com.server.cogito.domain.auth.service;

import com.server.cogito.domain.auth.dto.TokenResponse;
import com.server.cogito.domain.auth.dto.request.SignInRequest;
import com.server.cogito.domain.auth.dto.response.KaKaoUser;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.enums.Provider;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.security.AuthUser;
import com.server.cogito.global.common.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

import static com.server.cogito.global.common.entity.BaseEntity.Status.ACTIVE;
import static com.server.cogito.global.common.exception.user.UserErrorCode.USER_INVALID_REFRESH_TOKEN;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;
    private final KaKaoService kaKaoService;


    @Value("${jwt.refresh-expiration-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;


    //로그인
    @Transactional
    public TokenResponse signIn(SignInRequest dto){
        KaKaoUser oauthUser = kaKaoService.getKaKaoUser(dto.getAccessToken());
        User user = userRepository.findByEmailAndStatus(oauthUser.getEmail(), ACTIVE)
                .orElseGet(() -> createUser(oauthUser));

        AuthUser authUser = AuthUser.of(user);

        TokenResponse response = jwtProvider.createToken(authUser);

        saveRefreshToken(authUser.getUsername(), response.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        return response;
    }

    private User createUser(KaKaoUser oauthUser) {
        return userRepository.save(User.builder()
                .email(oauthUser.getEmail())
                .nickname(oauthUser.getNickname())
                .provider(Provider.KAKAO)
                .build());
    }

    @Transactional
    public void signOut(AuthUser authUser, String accessToken){
        // Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authUser.getUsername()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authUser.getUsername());
        }

        // Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtProvider.getExpiration(accessToken);
        saveAccessTokenInBlackList(accessToken,expiration);


    }
    @Transactional
    public TokenResponse reissue(AuthUser authUser, String refreshToken){

        //Refresh Token 검증
        if(!jwtProvider.validateToken(refreshToken)){
            log.error("유효하지않은 refreshToken 입니다.");
            throw new ApplicationException(USER_INVALID_REFRESH_TOKEN);
        }

        String redisRefreshToken = (String)redisTemplate.opsForValue().get("RT:" + authUser.getUsername());

        //Redis에 Refresh Token이 존재하지 않을 경우
        if(ObjectUtils.isEmpty(redisRefreshToken)){
            log.error("Redis에 refreshToken이 존재하지 않습니다.");
            throw new ApplicationException(USER_INVALID_REFRESH_TOKEN);
        }
        if(!redisRefreshToken.equals(refreshToken)){
            log.error("not equal");
            throw new ApplicationException(USER_INVALID_REFRESH_TOKEN);
        }

        TokenResponse result = jwtProvider.createToken(authUser);
        saveRefreshToken(authUser.getUsername(),result.getRefreshToken(),REFRESH_TOKEN_EXPIRE_TIME);

        return result;

    }

    private void saveRefreshToken(String key, String value, long expiration) {
        redisTemplate.opsForValue()
                .set("RT:" + key,
                        value,
                        expiration,
                        TimeUnit.MILLISECONDS);
    }

    private void saveAccessTokenInBlackList(String key, long expiration){
        redisTemplate.opsForValue()
                .set(key, "logout",expiration, TimeUnit.MILLISECONDS);
    }


}

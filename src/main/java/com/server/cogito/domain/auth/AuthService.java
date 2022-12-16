package com.server.cogito.domain.auth;

import com.server.cogito.domain.auth.dto.TokenResponse;
import com.server.cogito.domain.auth.dto.request.SignInRequest;
import com.server.cogito.domain.auth.dto.response.SignInResponse;
import com.server.cogito.global.common.entity.Status;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.security.AuthUser;
import com.server.cogito.global.common.security.jwt.JwtProvider;
import com.server.cogito.domain.user.UserRepository;
import com.server.cogito.domain.user.domain.KaKaoUser;
import com.server.cogito.domain.user.domain.Provider;
import com.server.cogito.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

import static com.server.cogito.global.common.exception.user.UserErrorCode.USER_INVALID_REFRESH_TOKEN;
import static com.server.cogito.global.common.exception.user.UserErrorCode.USER_NOT_EXIST;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;


    @Value("${jwt.refresh-expiration-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;


    //로그인
    public SignInResponse signIn(SignInRequest dto){
        KaKaoUser oauthUser = CreateKaKaoUser.createKaKaoUserInfo(dto.getToken());

        User user = userRepository.findByEmailAndStatus(oauthUser.getEmail(), Status.ACTIVE)
                .orElse(userRepository.save(User.builder().
                        email(oauthUser.getEmail())
                        .nickname(oauthUser.getNickname())
                        .provider(Provider.KAKAO)
                        .build()));

        user.addScore();

        AuthUser authUser = AuthUser.of(user);

        TokenResponse tokenResponse = jwtProvider.createToken(authUser);
        SignInResponse result = SignInResponse.of(user.getId(), tokenResponse);

        redisTemplate.opsForValue()
                .set("RT:" + authUser.getUsername(),
                        result.getRefreshToken(),
                        REFRESH_TOKEN_EXPIRE_TIME,
                        TimeUnit.MILLISECONDS);
        return result;


    }

    public void signOut(String email, String accessToken){
        User user = userRepository.findByEmailAndStatus(email, Status.ACTIVE)
                .orElseThrow(() -> new ApplicationException(USER_NOT_EXIST));

        // Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + user.getEmail()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + user.getEmail());
        }

        // Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);


    }

    public TokenResponse reissue(String refreshToken, AuthUser authUser){

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
        redisTemplate.opsForValue()
                .set("RT:" + authUser.getUsername(), result.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return result;

    }


}

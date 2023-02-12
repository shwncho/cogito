package com.server.cogito.auth.service;

import com.server.cogito.auth.domain.LogoutAccessToken;
import com.server.cogito.auth.domain.LogoutRefreshToken;
import com.server.cogito.auth.domain.RefreshToken;
import com.server.cogito.auth.dto.response.AccessTokenResponse;
import com.server.cogito.auth.dto.response.ReissueTokenResponse;
import com.server.cogito.auth.dto.result.LoginResult;
import com.server.cogito.auth.repository.TokenRepository;
import com.server.cogito.common.exception.auth.RefreshTokenInvalidException;
import com.server.cogito.common.exception.auth.RefreshTokenNotFoundException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.common.security.jwt.JwtProvider;
import com.server.cogito.infrastructure.oauth.OauthHandler;
import com.server.cogito.notification.repository.EmitterRepository;
import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.server.cogito.common.entity.BaseEntity.Status.ACTIVE;
import static com.server.cogito.user.enums.Provider.toEnum;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final OauthHandler oauthHandler;
    private final TokenRepository tokenRepository;
    private final EmitterRepository emitterRepository;

    //로그인
    @Transactional
    public LoginResult login(String provider, String code){

        OauthUserInfo oauthUserInfo = oauthHandler.getUserInfoFromCode(toEnum(provider),code);
        User user = userRepository.findByEmailAndStatus(oauthUserInfo.getEmail(), ACTIVE)
                .orElseGet(()-> createOauthUser(oauthUserInfo));

        AuthUser authUser = AuthUser.of(user);

        String accessToken = jwtProvider.createAccessToken(authUser);
        String refreshToken = createAndSaveRefreshToken(authUser);

        return LoginResult.of(accessToken,refreshToken, isRegistered(user.getNickname()));
    }

    private boolean isRegistered(String nickname){
        return nickname!=null;
    }

    private User createOauthUser(OauthUserInfo client){
        return userRepository.save(User.builder()
                .email(client.getEmail())
                .name(client.getName())
                .provider(client.getProvider())
                .build());
    }

    private String createAndSaveRefreshToken(AuthUser authUser) {
        String refreshToken = jwtProvider.createRefreshToken(authUser);
        tokenRepository.saveRefreshToken(
                RefreshToken.of(authUser.getUsername(),
                        refreshToken,
                        jwtProvider.getRemainingMilliSecondsFromToken(refreshToken)));
        return refreshToken;
    }

    @Transactional
    public void logout(AuthUser authUser,String accessToken, String refreshToken){
        LogoutAccessToken logoutAccessToken =
                LogoutAccessToken.of(accessToken, jwtProvider.getRemainingMilliSecondsFromToken(accessToken));
        LogoutRefreshToken logoutRefreshToken =
                LogoutRefreshToken.of(refreshToken, jwtProvider.getRemainingMilliSecondsFromToken(refreshToken));

        tokenRepository.saveLogoutAccessToken(logoutAccessToken);
        tokenRepository.saveLogoutRefreshToken(logoutRefreshToken);
        emitterRepository.deleteAllStartWithId(String.valueOf(authUser.getUserId()));
        emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(authUser.getUserId()));

    }
    @Transactional
    public ReissueTokenResponse reissue(AuthUser authUser, String refreshToken){
        RefreshToken redisRefreshToken = tokenRepository.findRefreshTokenByUsername(authUser.getUsername())
                        .orElseThrow(RefreshTokenNotFoundException::new);
        validateRefreshToken(refreshToken);
        tokenRepository.deleteRefreshToken(redisRefreshToken);
        return ReissueTokenResponse.of(jwtProvider.createAccessToken(authUser), createAndSaveRefreshToken(authUser));
    }

    private void validateRefreshToken(String refreshToken) {
        if(tokenRepository.existsLogoutRefreshTokenById(refreshToken)){
            throw new RefreshTokenInvalidException();
        }
    }


}

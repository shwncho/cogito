package com.server.cogito.auth.repository;

import com.server.cogito.auth.domain.LogoutAccessToken;
import com.server.cogito.auth.domain.LogoutRefreshToken;
import com.server.cogito.auth.domain.RefreshToken;

import java.util.Optional;

public interface TokenRepository {

    Optional<RefreshToken> findRefreshTokenByUsername(String username);

    boolean existsLogoutAccessTokenById(String id);

    boolean existsLogoutRefreshTokenById(String id);

    boolean existsRefreshTokenByUsername(String username);

    void saveRefreshToken(RefreshToken refreshToken);

    void saveLogoutAccessToken(LogoutAccessToken logoutAccessToken);

    void saveLogoutRefreshToken(LogoutRefreshToken logoutRefreshToken);

    void deleteRefreshToken(RefreshToken refreshToken);
}

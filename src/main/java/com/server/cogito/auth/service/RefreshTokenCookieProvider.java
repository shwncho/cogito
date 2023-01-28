package com.server.cogito.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookieProvider {

    public static final String REFRESH_TOKEN = "refreshToken";
    private static final int REMOVAL_MAX_AGE = 0;

    private final Long expiredTimeMillis;

    public RefreshTokenCookieProvider(@Value("${jwt.refresh-expiration-time}") Long expiredTimeMillis) {
        this.expiredTimeMillis = expiredTimeMillis;
    }

    public ResponseCookie createCookie(String refreshToken) {
        return createTokenCookieBuilder(refreshToken)
                .maxAge(Duration.ofMillis(expiredTimeMillis))
                .build();
    }

    public ResponseCookie createLogoutCookie() {
        return createTokenCookieBuilder("")
                .maxAge(REMOVAL_MAX_AGE)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder createTokenCookieBuilder(String value) {
        return ResponseCookie.from(REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Cookie.SameSite.NONE.attributeValue());
    }
}

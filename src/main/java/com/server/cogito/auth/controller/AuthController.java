package com.server.cogito.auth.controller;

import com.server.cogito.auth.dto.response.AccessTokenResponse;
import com.server.cogito.auth.dto.response.LoginResponse;
import com.server.cogito.auth.dto.response.ReissueTokenResponse;
import com.server.cogito.auth.dto.result.LoginResult;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.auth.service.RefreshTokenCookieProvider;
import com.server.cogito.common.exception.auth.RefreshTokenNotFoundException;
import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.server.cogito.auth.service.RefreshTokenCookieProvider.REFRESH_TOKEN;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @GetMapping("/{provider}/login/token")
    public ResponseEntity<LoginResponse> login(@PathVariable String provider, @RequestParam String code) {
        LoginResult loginResult = authService.login(provider,code);
        String refreshToken = loginResult.getRefreshToken();
        ResponseCookie cookie = refreshTokenCookieProvider.createCookie(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(LoginResponse.from(loginResult));
    }

    @PostMapping("/logout")
    public void logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @CookieValue(value=REFRESH_TOKEN, required = false) String refreshToken
    ){
        validateRefreshTokenExists(refreshToken);
        authService.logout(removeType(accessToken),refreshToken);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
            @AuthenticationPrincipal AuthUser authUser,
            @CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken
    ){
        validateRefreshTokenExists(refreshToken);
        ReissueTokenResponse token = authService.reissue(authUser, refreshToken);
        ResponseCookie responseCookie = refreshTokenCookieProvider.createCookie(token.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AccessTokenResponse(token.getAccessToken()));
    }

    private void validateRefreshTokenExists(String refreshToken){
        if(refreshToken==null){
            throw new RefreshTokenNotFoundException();
        }
    }

    private String removeType(String token) {
        return token.substring(7);
    }
}

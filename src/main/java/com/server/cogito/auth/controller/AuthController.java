package com.server.cogito.auth.controller;

import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/{provider}/login/token")
    public TokenResponse login(@PathVariable String provider, @RequestParam String code) {
        return authService.login(provider,code);
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ){
        authService.logout(authUser, removeType(accessToken));
    }

    private String removeType(String token) {
        return token.substring(7);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(@AuthenticationPrincipal AuthUser authUser,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken
    ){
        return authService.reissue(authUser, removeType(refreshToken));
    }
}

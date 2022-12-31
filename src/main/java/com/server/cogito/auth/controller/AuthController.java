package com.server.cogito.auth.controller;

import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.auth.dto.request.SignInRequest;
import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public TokenResponse signIn(@RequestBody @Valid SignInRequest request){
        return authService.signIn(request);
    }

    @PostMapping("/sign-out")
    public void signOut(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ){
        authService.signOut(authUser, removeType(accessToken));
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

    @GetMapping("/{provider}")
    public TokenResponse login(@PathVariable String provider, @RequestParam String code) {
        return authService.login(provider,code);
    }


}

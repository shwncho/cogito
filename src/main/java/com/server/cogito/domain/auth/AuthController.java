package com.server.cogito.domain.auth;

import com.server.cogito.domain.auth.dto.TokenResponse;
import com.server.cogito.domain.auth.dto.request.SignInRequest;
import com.server.cogito.domain.auth.dto.response.SignInResponse;
import com.server.cogito.global.common.security.AuthUser;
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
    public SignInResponse signIn(@RequestBody @Valid SignInRequest signInRequest){
        return authService.signIn(signInRequest);
    }

    @PostMapping("/sign-out")
    public void signOut(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken
    ){
        authService.signOut(authUser.getUsername(), removeType(accessToken));
    }

    private String removeType(String token) {
        return token.substring(7);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken,
                                 @AuthenticationPrincipal AuthUser authUser){
        return authService.reissue(removeType(refreshToken), authUser);
    }




}

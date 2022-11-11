package com.server.cogito.auth;

import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.auth.dto.request.SignInRequest;
import com.server.cogito.auth.dto.request.SignOutRequest;
import com.server.cogito.auth.dto.response.SignInResponse;
import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody @Valid SignOutRequest signOutRequest
    ){
        authService.signOut(authUser.getUsername(), signOutRequest);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody @Valid TokenResponse tokenResponse){
        return authService.reissue(tokenResponse);
    }




}

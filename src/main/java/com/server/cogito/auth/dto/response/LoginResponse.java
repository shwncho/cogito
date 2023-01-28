package com.server.cogito.auth.dto.response;

import com.server.cogito.auth.dto.result.LoginResult;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private boolean registered;

    public static LoginResponse from(LoginResult loginResult){
        return LoginResponse.builder()
                .accessToken(loginResult.getAccessToken())
                .registered(loginResult.isRegistered())
                .build();
    }

}
package com.server.cogito.auth.dto.response;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private TokenResponse token;

    private boolean registered;

    public static LoginResponse from(TokenResponse token, boolean registered){
        return LoginResponse.builder()
                .token(token)
                .registered(registered)
                .build();
    }

}
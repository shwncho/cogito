package com.server.cogito.auth.dto.result;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResult {

    private String accessToken;

    private String refreshToken;

    private boolean registered;

    public static LoginResult of(String accessToken, String refreshToken, boolean registered){
        return LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .registered(registered)
                .build();
    }
}

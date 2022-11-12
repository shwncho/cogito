package com.server.cogito.domain.auth.dto.response;

import com.server.cogito.domain.auth.dto.TokenResponse;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class SignInResponse {

    private Long userId;

    private String accessToken;

    private String refreshToken;

    public static SignInResponse of(Long userId, TokenResponse tokenResponse){
        return SignInResponse.builder()
                .userId(userId)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }

}

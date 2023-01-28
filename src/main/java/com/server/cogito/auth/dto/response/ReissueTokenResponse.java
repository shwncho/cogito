package com.server.cogito.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReissueTokenResponse {

    private String accessToken;

    private String refreshToken;

    public static ReissueTokenResponse of(String accessToken, String refreshToken){
        return ReissueTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}

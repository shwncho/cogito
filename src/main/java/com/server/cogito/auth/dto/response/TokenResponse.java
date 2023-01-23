package com.server.cogito.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;

    private String refreshToken;
}

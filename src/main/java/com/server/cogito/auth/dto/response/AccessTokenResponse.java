package com.server.cogito.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;

}

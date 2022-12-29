package com.server.cogito.domain.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoResponse {

    private String token_type;
    private String access_token;
    private String expired_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;

}

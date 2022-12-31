package com.server.cogito.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoTokenResponse {

    @JsonProperty("access_token")
    private String access_token;

}

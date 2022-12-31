package com.server.cogito.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
}

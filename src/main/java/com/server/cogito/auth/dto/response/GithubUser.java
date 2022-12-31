package com.server.cogito.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubUser {

    @JsonProperty("email")
    private String email;
    @JsonProperty("id")
    private String githubId;
}

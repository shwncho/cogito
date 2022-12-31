package com.server.cogito.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.cogito.user.enums.Provider;


public class GithubOAuthClient implements OAuthClient{
    @JsonProperty("email")
    private String email;
    @JsonProperty("id")
    private String githubId;
    @JsonProperty("name")
    private String nickname;

    @Override
    public String getEmail() {
        if(email == null) return getGithubId();
        return email;
    }

    @Override
    public Provider getProvider() {
        return Provider.GITHUB;
    }

    public String getGithubId() {
        return githubId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }
}

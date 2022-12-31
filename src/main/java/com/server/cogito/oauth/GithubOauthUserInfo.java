package com.server.cogito.oauth;

import com.server.cogito.user.enums.Provider;

import java.util.Collections;
import java.util.Map;


public class GithubOauthUserInfo implements OauthUserInfo {
    private final Map<String, Object> attributes;

    private GithubOauthUserInfo(Map<String, Object> attributes){
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    public static GithubOauthUserInfo from(Map<String, Object> attributes){
        return new GithubOauthUserInfo(attributes);
    }

    @Override
    public String getEmail(){
        if (attributes.get("email")==null)  return getGithubId();
        return attributes.get("email").toString();
    }
    @Override
    public Provider getProvider() {
        return Provider.GITHUB;
    }

    public String getGithubId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("name").toString();
    }
}

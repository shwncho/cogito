package com.server.cogito.oauth;

import com.server.cogito.common.exception.infrastructure.NoPublicEmailOnGithubException;
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
        validatePublicEmail();
        return attributes.get("email").toString();
    }
    @Override
    public Provider getProvider() {
        return Provider.GITHUB;
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    private void validatePublicEmail(){
        if(attributes.get("email")==null){
            throw new NoPublicEmailOnGithubException();
        }
    }
}

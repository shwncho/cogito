package com.server.cogito.oauth;

import com.server.cogito.user.enums.Provider;

public interface OAuthClient {

    String getEmail();
    String getNickname();
    Provider getProvider();
}

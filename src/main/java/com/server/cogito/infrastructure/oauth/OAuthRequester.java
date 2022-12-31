package com.server.cogito.infrastructure.oauth;

import com.server.cogito.oauth.OAuthClient;
import com.server.cogito.user.enums.Provider;

public interface OAuthRequester {
    boolean supports(Provider provider);

    OAuthClient getUserInfoByCode(String code);
}

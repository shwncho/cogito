package com.server.cogito.infrastructure.oauth;

import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.enums.Provider;

public interface OauthRequester {
    boolean supports(Provider provider);

    OauthUserInfo getUserInfoByCode(String code);
}

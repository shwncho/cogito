package com.server.cogito.oauth;

import com.server.cogito.user.enums.Provider;

public interface OauthUserInfo {
    String getEmail();
    String getName();
    Provider getProvider();
}

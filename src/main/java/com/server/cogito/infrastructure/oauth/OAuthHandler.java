package com.server.cogito.infrastructure.oauth;

import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.oauth.OAuthClient;
import com.server.cogito.user.enums.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuthHandler {
    private final List<OAuthRequester> oAuthRequesters;

    public OAuthHandler(final List<OAuthRequester> oauthRequesters) {
        this.oAuthRequesters = oauthRequesters;
    }

    public OAuthClient getUserInfoFromCode(final Provider provider, final String code) {
        OAuthRequester requester = getRequester(provider);
        return requester.getUserInfoByCode(code);
    }

    private OAuthRequester getRequester(final Provider provider) {
        return oAuthRequesters.stream()
                .filter(requester -> requester.supports(provider))
                .findFirst()
                .orElseThrow(UnsupportedOauthProviderException::new);
    }
}

package com.server.cogito.infrastructure.oauth;

import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.enums.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OauthHandler {
    private final List<OauthRequester> oauthRequesters;

    public OauthHandler(final List<OauthRequester> oauthRequesters) {
        this.oauthRequesters = oauthRequesters;
    }

    public OauthUserInfo getUserInfoFromCode(final Provider provider, final String code) {
        OauthRequester requester = getRequester(provider);
        return requester.getUserInfoByCode(code);
    }

    private OauthRequester getRequester(final Provider provider) {
        return oauthRequesters.stream()
                .filter(requester -> requester.supports(provider))
                .findFirst()
                .orElseThrow(UnsupportedOauthProviderException::new);
    }
}

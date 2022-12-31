package com.server.cogito.infrastructure.oauth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.auth.AuthErrorCode;
import com.server.cogito.oauth.GithubOAuthClient;
import com.server.cogito.oauth.OAuthClient;
import com.server.cogito.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRequester implements OAuthRequester{

    @Value("${spring.github.client.id}")
    private String CLIENT_ID;
    @Value("${spring.github.client.secret}")
    private String CLIENT_SECRET;
    @Value("${spring.github.token}")
    private String TOKEN_URL;
    @Value("${spring.github.profile}")
    private String PROFILE_URL;

    private final RestTemplate restTemplate;

    @Override
    public boolean supports(Provider provider) {return provider.isSameAs(Provider.GITHUB);
    }

    @Override
    public OAuthClient getUserInfoByCode(String code) {
        return getGithubProfile(getAccessToken(code));
    }


    private String getAccessToken(String code) {
        GithubAccessTokenRequest request = new GithubAccessTokenRequest(code, CLIENT_ID, CLIENT_SECRET);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);

        GithubTokenResponse response = exchangeRestTemplateBody(TOKEN_URL, HttpMethod.POST,
                httpEntity, GithubTokenResponse.class);
        if (Objects.isNull(response)) {
            log.error("github oauth error. clientId = {}, clientSecret = {}, tokenUrl = {}, profileUrl = {}", CLIENT_ID,
                    CLIENT_SECRET, TOKEN_URL, PROFILE_URL);
            throw new ApplicationException(AuthErrorCode.GITHUB_LOGIN);
        }
        return response.getAccessToken();
    }

    private GithubOAuthClient getGithubProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        return exchangeRestTemplateBody(PROFILE_URL, HttpMethod.GET, httpEntity, GithubOAuthClient.class);
    }


    private <T> T exchangeRestTemplateBody(final String url, final HttpMethod httpMethod,
                                           final HttpEntity<?> httpEntity, final Class<T> exchangeType) {
        try {
            return restTemplate
                    .exchange(url, httpMethod, httpEntity, exchangeType)
                    .getBody();
        } catch (HttpClientErrorException | NullPointerException e) {
            log.error(
                    "github oauth error. clientId = {}, clientSecret = {}, tokenUrl = {}, profileUrl = {}, message = {}",
                    CLIENT_ID, CLIENT_SECRET, TOKEN_URL, PROFILE_URL, e.getMessage());
            throw new ApplicationException(AuthErrorCode.GITHUB_LOGIN);
        }
    }

}

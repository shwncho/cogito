package com.server.cogito.infrastructure.oauth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.auth.AuthErrorCode;
import com.server.cogito.oauth.GithubOauthUserInfo;
import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRequester implements OauthRequester {

    @Value("${spring.github.client.id}")
    private String CLIENT_ID;
    @Value("${spring.github.client.secret}")
    private String CLIENT_SECRET;
    @Value("${spring.github.token}")
    private String TOKEN_URL;
    @Value("${spring.github.profile}")
    private String PROFILE_URL;

    private final WebClient githubOauthLoginClient;
    private final WebClient githubOpenApiClient;

    @Override
    public boolean supports(Provider provider) {return provider.isSameAs(Provider.GITHUB);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(String code) {
        return getUserInfo(getAccessToken(code));
    }


    private String getAccessToken(String code) {
        Map<String, Object> responseBody = requestAccessToken(githubOauthLoginClient)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client_id",CLIENT_ID)
                        .queryParam("client_secret",CLIENT_SECRET)
                        .queryParam("code",code)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(()->new ApplicationException(AuthErrorCode.GITHUB_LOGIN));
        validateResponseBody(responseBody);
        return responseBody.get("access_token").toString();
    }

    private void validateResponseBody(Map<String, Object> responseBody) {
        if (!responseBody.containsKey("access_token")) {
            throw new ApplicationException(AuthErrorCode.GITHUB_LOGIN);
        }
    }

    private GithubOauthUserInfo getUserInfo(String accessToken) {
        Map<String, Object> responseBody = requestGithubClient(githubOpenApiClient)
                .get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(()->new ApplicationException(AuthErrorCode.GITHUB_LOGIN));
        return GithubOauthUserInfo.from(responseBody);
    }

    private WebClient requestAccessToken(final WebClient webClient) {
        return webClient.mutate()
                .baseUrl(TOKEN_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient requestGithubClient(final WebClient webClient) {
        return webClient.mutate()
                .baseUrl(PROFILE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}

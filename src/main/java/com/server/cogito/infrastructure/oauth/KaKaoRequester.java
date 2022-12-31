package com.server.cogito.infrastructure.oauth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.oauth.KakaoOauthUserInfo;
import com.server.cogito.oauth.OauthUserInfo;
import com.server.cogito.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static com.server.cogito.common.exception.auth.AuthErrorCode.KAKAO_LOGIN;

@Component
@RequiredArgsConstructor
public class KaKaoRequester implements OauthRequester {

    @Value("${spring.kakao.client}")
    private String CLIENT_ID;

    @Value("${spring.kakao.redirect}")
    private String REDIRECT_URI;

    @Value("${spring.kakao.token}")
    private String TOKEN_URL;

    @Value("${spring.kakao.profile}")
    private String PROFILE_URL;

    private final WebClient kakaoOauthLoginClient;
    private final WebClient kakaoUserClient;

    @Override
    public boolean supports(Provider provider) {
        return provider.isSameAs(Provider.KAKAO);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(String code) {
        return getUserInfo(getAccessToken(code));
    }

    private String getAccessToken(String code){
        Map<String, Object> responseBody = requestAccessToken(kakaoOauthLoginClient)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type","authorization_code")
                        .queryParam("client_id",CLIENT_ID)
                        .queryParam("redirect_uri",REDIRECT_URI)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(()->new ApplicationException(KAKAO_LOGIN));
        validateResponseBody(responseBody);
        return responseBody.get("access_token").toString();

    }

    private void validateResponseBody(Map<String, Object> responseBody) {
        if (!responseBody.containsKey("access_token")) {
            throw new ApplicationException(KAKAO_LOGIN);
        }
    }

    public KakaoOauthUserInfo getUserInfo(String accessToken){
        Map<String, Object> responseBody = requestKaKaoClient(kakaoUserClient)
                .get()
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(()->new ApplicationException(KAKAO_LOGIN));

        return KakaoOauthUserInfo.from(responseBody);

    }


    private WebClient requestAccessToken(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(TOKEN_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient requestKaKaoClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(PROFILE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}

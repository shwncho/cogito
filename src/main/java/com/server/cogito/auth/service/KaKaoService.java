package com.server.cogito.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.server.cogito.auth.dto.response.KaKaoTokenResponse;
import com.server.cogito.auth.dto.response.KaKaoUser;
import com.server.cogito.common.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.server.cogito.common.exception.auth.AuthErrorCode.KAKAO_LOGIN;

@Service
@RequiredArgsConstructor
public class KaKaoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kakao.client}")
    private String CLIENT_ID;

    @Value("${spring.kakao.redirect}")
    private String REDIRECT_URI;

    @Value("${spring.kakao.token}")
    private String TOKEN_URL;

    @Value("${spring.kakao.profile}")
    private String PROFILE_URL;

    public String getKaKaoAccessToken(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id",CLIENT_ID);
        params.add("redirect_uri",REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        try {
            return objectMapper.readValue(response.getBody(), KaKaoTokenResponse.class).getAccess_token();
        } catch (JsonProcessingException e) {
            throw new ApplicationException(KAKAO_LOGIN);
        }
    }

    public KaKaoUser getKaKaoUser(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer "+accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(PROFILE_URL, request, String.class);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.getBody());
            return KaKaoUser.builder()
                    .email(element.getAsJsonObject().get("kakao_account")
                            .getAsJsonObject().get("email").getAsString())
                    .nickname(element.getAsJsonObject().get("kakao_account")
                            .getAsJsonObject().get("profile")
                            .getAsJsonObject().get("nickname").getAsString())
                    .build();
        } catch (Exception e) {
            throw new ApplicationException(KAKAO_LOGIN);
        }
    }

}

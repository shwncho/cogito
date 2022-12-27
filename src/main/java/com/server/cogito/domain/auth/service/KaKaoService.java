package com.server.cogito.domain.auth.service;

import com.google.gson.Gson;
import com.server.cogito.domain.auth.dto.response.KaKaoUser;
import com.server.cogito.global.common.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.server.cogito.global.common.exception.auth.AuthErrorCode.KAKAO_LOGIN;

@Service
@RequiredArgsConstructor
public class KaKaoService {

    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${spring.kakao.profile}")
    private String kakaoProfileUrl;

    public KaKaoUser getKaKaoUser(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoProfileUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(response.getBody(), KaKaoUser.class);
        } catch (Exception e) {
            throw new ApplicationException(KAKAO_LOGIN);
        }
        throw new ApplicationException(KAKAO_LOGIN);
    }

}

package com.server.cogito.oauth;

import com.server.cogito.user.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoOAuthClient implements OAuthClient{

    private String email;
    private String nickname;

    public static KakaoOAuthClient of(String email, String nickname){
        return KakaoOAuthClient.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }
}

package com.server.cogito.oauth;

import com.server.cogito.user.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoOauthUserInfo implements OauthUserInfo {
    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    private KakaoOauthUserInfo(Map<String, Object> attributes){
        this.attributes = Collections.unmodifiableMap(attributes);
        this.attributesAccount = Collections.unmodifiableMap((Map<String, Object>) attributes.get("kakao_account"));
        this.attributesProfile= Collections.unmodifiableMap((Map<String, Object>) attributesAccount.get("profile"));
    }

    public static KakaoOauthUserInfo from(Map<String, Object> attributes){
        return new KakaoOauthUserInfo(attributes);
    }

    @Override
    public String getEmail() {
        return attributesAccount.get("email").toString();
    }

    @Override
    public String getNickname() {
        return attributesProfile.get("nickname").toString();
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }
}

package com.server.cogito.domain.auth.dto.request;

import com.server.cogito.domain.user.enums.Provider;
import com.server.cogito.global.common.validator.EnumValid;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {

    @NotNull(message = "oauth accessToken을 입력해주세요.")
    private String accessToken;

    @EnumValid(message = "지원하지 않는 소셜 로그인 방식입니다.", enumClass = SocialLoginProvider.class)
    private String provider;

    @Getter
    @AllArgsConstructor
    public enum SocialLoginProvider {

        KAKAO("KAKAO"),
        GITHUB("GITHUB"),
        ;

        private final String providerValue;
    }

}

package com.server.cogito.domain.auth.dto.request;

import com.server.cogito.domain.user.enums.Provider;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {

    @NotNull(message = "oauth token을 입력해주세요.")
    private String token;

    @NotNull(message = "oauth 제공자를 입력해주세요.")
    private Provider provider;

}

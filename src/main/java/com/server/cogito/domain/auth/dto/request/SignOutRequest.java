package com.server.cogito.domain.auth.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignOutRequest {

    @NotEmpty(message = "accessToken 을 입력해주세요.")
    private String accessToken;

    @NotEmpty(message = "refreshToken 을 입력해주세요.")
    private String refreshToken;
}

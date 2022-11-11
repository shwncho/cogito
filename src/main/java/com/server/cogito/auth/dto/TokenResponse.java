package com.server.cogito.auth.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    @NotEmpty(message = "accessToken 을 입력해주세요.")
    private String accessToken;

    @NotEmpty(message = "refreshToken 을 입력해주세요.")
    private String refreshToken;

}
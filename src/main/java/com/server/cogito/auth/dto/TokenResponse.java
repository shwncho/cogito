package com.server.cogito.auth.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

}
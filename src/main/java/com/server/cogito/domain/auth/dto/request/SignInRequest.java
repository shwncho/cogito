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

    @NotBlank
    private String token;

    @NotNull
    private Provider provider;

}

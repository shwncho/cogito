package com.server.cogito.auth.dto.request;

import com.server.cogito.user.domain.Provider;
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

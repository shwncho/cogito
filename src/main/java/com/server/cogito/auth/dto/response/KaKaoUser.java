package com.server.cogito.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KaKaoUser {

    private String email;
    private String nickname;

    public static KaKaoUser of(String email, String nickname){
        return KaKaoUser.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }
}

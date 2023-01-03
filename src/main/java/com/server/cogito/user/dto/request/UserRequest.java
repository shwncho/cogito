package com.server.cogito.user.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private String nickname;

    private String profileImgUrl;

    private String introduce;
}

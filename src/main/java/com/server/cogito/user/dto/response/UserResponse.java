package com.server.cogito.user.dto.response;

import com.server.cogito.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String nickname;

    private String profileImgUrl;

    private int score;

    private String introduce;

    public static UserResponse from(User user){
        return UserResponse.builder()
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .score(user.getScore())
                .introduce(user.getIntroduce())
                .build();
    }
}

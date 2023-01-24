package com.server.cogito.user.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPageResponse {

    private List<UserResponse> users = new ArrayList<>();

    private long total;

    public static UserPageResponse of(List<UserResponse> users, long total){
        return UserPageResponse.builder()
                .users(users)
                .total(total)
                .build();
    }
}

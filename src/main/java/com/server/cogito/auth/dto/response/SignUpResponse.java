package com.server.cogito.auth.dto.response;

import com.server.cogito.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {

    private Long userId;


    public SignUpResponse(User user){
        this.userId = user.getId();
    }


}

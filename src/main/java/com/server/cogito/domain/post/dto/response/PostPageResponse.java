package com.server.cogito.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPageResponse {

    private List<PostInfo> posts;

    public static PostPageResponse from(List<PostInfo> posts){
        return PostPageResponse.builder()
                .posts(posts)
                .build();
    }
}

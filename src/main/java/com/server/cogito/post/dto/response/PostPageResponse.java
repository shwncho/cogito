package com.server.cogito.post.dto.response;

import lombok.*;

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

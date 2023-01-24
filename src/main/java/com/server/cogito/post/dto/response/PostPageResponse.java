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

    private long total;

    public static PostPageResponse of(List<PostInfo> posts, long total){
        return PostPageResponse.builder()
                .posts(posts)
                .total(total)
                .build();
    }
}

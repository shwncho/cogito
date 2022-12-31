package com.server.cogito.post.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostResponse {

    private Long postId;

    public static CreatePostResponse from(Long postId){
        return CreatePostResponse.builder()
                .postId(postId)
                .build();
    }
}

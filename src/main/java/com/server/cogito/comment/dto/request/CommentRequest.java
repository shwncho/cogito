package com.server.cogito.comment.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    private Long postId;

    private Long parentId;

    private String content;
}

package com.server.cogito.comment.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRequest {

    @NotBlank(message = "수정할 댓글 내용을 입력해주세요.")
    private String content;
}

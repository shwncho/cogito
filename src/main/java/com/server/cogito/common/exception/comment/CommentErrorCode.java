package com.server.cogito.common.exception.comment;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorEnumCode {

    COMMENT_NOT_FOUND("C001","존재하지 않는 댓글입니다."),
    ;

    private final String code;
    private String message;
}

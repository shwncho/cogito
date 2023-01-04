package com.server.cogito.common.exception.post;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorEnumCode {

    POST_NOT_FOUND("P001", "존재하지 않는 게시물입니다."),
    ;
    private final String code;
    private String message;
}

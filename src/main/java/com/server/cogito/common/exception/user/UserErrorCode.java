package com.server.cogito.common.exception.user;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorEnumCode {

    USER_NOT_FOUND("U001","존재하지 않는 유저입니다."),
    USER_NICKNAME_EXIST("U002", "이미 존재하는 닉네임입니다."),
    ;

    private final String code;
    private String message;
}

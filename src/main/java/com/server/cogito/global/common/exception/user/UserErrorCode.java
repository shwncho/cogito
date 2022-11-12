package com.server.cogito.global.common.exception.user;

import com.server.cogito.global.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorEnumCode {

    USER_NOT_EXIST("U001","존재하지 않는 유저입니다."),
    USER_EXIST_USERNAME("U002","이미 존재하는 사용자 이름입니다."),
    USER_INVALID_PASSWORD("U003","비밀번호가 일치하지 않습니다."),
    USER_INVALID_OAUTH("U004","지원하지않는 oauth 로그인 형식입니다."),
    USER_NOT_INVALID("U005","권한이 없는 유저입니다."),
    USER_INVALID_REFRESH_TOKEN("U006", "유효하지않은 Refresh Token 입니다.");

    private final String code;
    private String message;
}

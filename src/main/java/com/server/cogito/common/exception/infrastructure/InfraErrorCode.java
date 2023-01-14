package com.server.cogito.common.exception.infrastructure;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfraErrorCode implements ErrorEnumCode {
    INVALID_OAUTH("I001","지원되지 않는 Oauth 제공자입니다."),
    EMPTY_PUBLIC_EMAIL("I002","소셜 로그인에 실패했습니다. 깃허브의 Public Email(Setting -> Profile)을 설정해주시기 바랍니다."),
    ;
    private final String code;
    private final String message;
}

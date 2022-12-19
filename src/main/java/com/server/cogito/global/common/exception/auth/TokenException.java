package com.server.cogito.global.common.exception.auth;

import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.exception.ErrorEnumCode;

public class TokenException extends ApplicationException {

    private static final String MESSAGE = "만료된 토큰입니다.";
    private ErrorEnumCode errorEnumCode;

    private TokenException(ErrorEnumCode errorEnumCode, String message) {
        super(errorEnumCode, message);
    }

    public TokenException(ErrorEnumCode errorEnumCode) {
        this(errorEnumCode, MESSAGE);
    }

    public TokenException() {
        this(AuthErrorCode.UNAUTHORIZED, MESSAGE);
    }
}

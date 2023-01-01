package com.server.cogito.common.exception.auth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class TokenException extends ApplicationException {

    private static final String MESSAGE = "만료된 토큰입니다.";

    private TokenException(ErrorEnumCode errorEnumCode, String message) {
        super(errorEnumCode, message);
    }

    public TokenException(ErrorEnumCode errorEnumCode) {
        this(errorEnumCode, MESSAGE);
    }

}


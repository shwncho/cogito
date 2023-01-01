package com.server.cogito.common.exception.auth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class RefreshTokenNotEqualException extends ApplicationException {

    private static final AuthErrorCode CODE = AuthErrorCode.NOT_EQUAL_REFRESH_TOKEN;

    public RefreshTokenNotEqualException(){
        this(CODE,CODE.getMessage());
    }
    private RefreshTokenNotEqualException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }
}

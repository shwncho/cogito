package com.server.cogito.common.exception.auth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class RefreshTokenInvalidException extends ApplicationException {

    private static final AuthErrorCode CODE = AuthErrorCode.INVALID_REFRESH_TOKEN;

    public RefreshTokenInvalidException(){
        this(CODE,CODE.getMessage());
    }
    private RefreshTokenInvalidException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }


}

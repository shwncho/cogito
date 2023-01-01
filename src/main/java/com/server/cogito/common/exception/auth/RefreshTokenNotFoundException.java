package com.server.cogito.common.exception.auth;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class RefreshTokenNotFoundException extends ApplicationException {

    private static final AuthErrorCode CODE = AuthErrorCode.NOT_FOUND_REFRESH_TOKEN;

    public RefreshTokenNotFoundException(){
        this(CODE,CODE.getMessage());
    }
    private RefreshTokenNotFoundException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

}

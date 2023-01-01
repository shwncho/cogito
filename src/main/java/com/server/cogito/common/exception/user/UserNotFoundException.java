package com.server.cogito.common.exception.user;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class UserNotFoundException extends ApplicationException {

    private static final UserErrorCode CODE = UserErrorCode.USER_NOT_FOUND;

    public UserNotFoundException(){
        this(CODE,CODE.getMessage());
    }

    private UserNotFoundException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }
}

package com.server.cogito.common.exception.user;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class UserInvalidException extends ApplicationException {


    private UserInvalidException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public UserInvalidException(ErrorEnumCode errorEnumCode){
        this(errorEnumCode, errorEnumCode.getMessage());
    }
}

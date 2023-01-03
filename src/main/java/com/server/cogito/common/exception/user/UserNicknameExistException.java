package com.server.cogito.common.exception.user;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class UserNicknameExistException extends ApplicationException {

    private static final UserErrorCode CODE = UserErrorCode.USER_NICKNAME_EXIST;

    public UserNicknameExistException(ErrorEnumCode errorEnumCode){
        super(CODE, CODE.getMessage());
    }
}

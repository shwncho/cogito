package com.server.cogito.common.exception.infrastructure;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class NoPublicEmailOnGithubException extends ApplicationException {

    private static final InfraErrorCode CODE = InfraErrorCode.EMPTY_PUBLIC_EMAIL;

    private NoPublicEmailOnGithubException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public NoPublicEmailOnGithubException(){
        this(CODE,CODE.getMessage());
    }
}

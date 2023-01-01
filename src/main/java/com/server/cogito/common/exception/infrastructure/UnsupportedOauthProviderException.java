package com.server.cogito.common.exception.infrastructure;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class UnsupportedOauthProviderException extends ApplicationException {

    private static final InfraErrorCode CODE=InfraErrorCode.INVALID_OAUTH;

    private UnsupportedOauthProviderException(ErrorEnumCode errorEnumCode, String message) {super(errorEnumCode, message);}

    public UnsupportedOauthProviderException() { this(CODE, CODE.getMessage());}
}

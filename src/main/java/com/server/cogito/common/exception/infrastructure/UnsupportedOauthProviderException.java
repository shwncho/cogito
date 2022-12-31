package com.server.cogito.common.exception.infrastructure;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;
import com.server.cogito.common.exception.auth.AuthErrorCode;

public class UnsupportedOauthProviderException extends ApplicationException {

    private static final String MESSAGE = "지원되지 않는 Oauth 제공자입니다.";
    private static final AuthErrorCode AUTH_ERROR_CODE = AuthErrorCode.INVALID_OAUTH;

    private UnsupportedOauthProviderException(ErrorEnumCode errorEnumCode, String message) {super(errorEnumCode, message);}

    public UnsupportedOauthProviderException() { this(AUTH_ERROR_CODE, MESSAGE);}

}

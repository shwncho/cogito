package com.server.cogito.common.exception.notification;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class NotificationNotFoundException extends ApplicationException {

    private static final NotificationErrorCode CODE = NotificationErrorCode.NOTIFICATION_NOT_FOUND;

    private NotificationNotFoundException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public NotificationNotFoundException(){
        this(CODE, CODE.getMessage());
    }
}

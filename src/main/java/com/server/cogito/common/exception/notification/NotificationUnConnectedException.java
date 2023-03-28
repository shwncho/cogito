package com.server.cogito.common.exception.notification;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class NotificationUnConnectedException extends ApplicationException {

    private static final NotificationErrorCode CODE = NotificationErrorCode.NOTIFICATION_UNCONNECTED;

    private NotificationUnConnectedException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode,message);
    }
    public NotificationUnConnectedException(){this(CODE,CODE.getMessage());}
}

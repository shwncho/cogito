package com.server.cogito.common.exception.notification;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorEnumCode {

    NOTIFICATION_NOT_FOUND("N001","존재하지 않는 알림입니다."),
    NOTIFICATION_UNCONNECTED("N002","SSE 연결에 실패했습니다.")
    ;
    private final String code;
    private String message;
}

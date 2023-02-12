package com.server.cogito.notification.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponses {

    /**
     * 로그인 한 유저의 모든 알림
     */
    private List<NotificationResponse> notificationResponses;

    /**
     * 로그인 한 유저가 읽지 않은 알림 수
     */
    private long unreadCount;

    public static NotificationResponses of(List<NotificationResponse> notificationResponses, long count) {
        return NotificationResponses.builder()
                .notificationResponses(notificationResponses)
                .unreadCount(count)
                .build();
    }
}

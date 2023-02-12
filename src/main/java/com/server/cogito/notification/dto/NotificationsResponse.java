package com.server.cogito.notification.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationsResponse {

    /**
     * 로그인 한 유저의 모든 알림
     */
    private List<NotificationResponse> notificationResponses;

    /**
     * 로그인 한 유저가 읽지 않은 알림 수
     */
    private long unreadCount;

    public static NotificationsResponse of(List<NotificationResponse> notificationResponses, long count) {
        return NotificationsResponse.builder()
                .notificationResponses(notificationResponses)
                .unreadCount(count)
                .build();
    }
}

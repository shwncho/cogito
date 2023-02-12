package com.server.cogito.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.cogito.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    /**
     * 알림 id
     */
    private Long id;

    /**
     * 알림 내용
     */
    private String content;

    /**
     * 알림 클릭 시 이동할 url
     */
    private String url;

    /**
     * 알림이 생성된 날짜
     */
    private LocalDateTime createdAt;

    /**
     * 알림 읽음 여부
     */
    @JsonProperty("isRead")
    private boolean isRead;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}

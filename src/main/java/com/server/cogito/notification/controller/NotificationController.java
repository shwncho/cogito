package com.server.cogito.notification.controller;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.notification.dto.NotificationsResponse;
import com.server.cogito.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUser authUser,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(authUser, lastEventId);
    }


    @GetMapping("")
    public NotificationsResponse getNotifications(@AuthenticationPrincipal AuthUser authUser) {
        return notificationService.getNotifications(authUser);
    }


    @PatchMapping("/notifications/{id}")
    public void readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
    }
}

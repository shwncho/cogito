package com.server.cogito.notification.service;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.exception.notification.NotificationNotFoundException;
import com.server.cogito.common.exception.notification.NotificationUnConnectedException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.notification.dto.NotificationResponse;
import com.server.cogito.notification.dto.NotificationResponses;
import com.server.cogito.notification.entity.Notification;
import com.server.cogito.notification.repository.EmitterRepository;
import com.server.cogito.notification.repository.NotificationRepository;
import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(AuthUser authUser, String lastEventId) {
        Long userId = authUser.getUserId();
        String id = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            log.error("SSE 연결 오류", exception);
            throw new NotificationUnConnectedException();
        }
    }

    @Transactional
    public void send(User receiver, Post post, Comment comment, String content) {
        Notification notification = createNotification(receiver, post, comment, content);
        String id = String.valueOf(receiver.getId());
        notificationRepository.save(notification);
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, NotificationResponse.from(notification));
                }
        );
    }

    private Notification createNotification(User receiver, Post post, Comment comment, String content) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .post(post)
                .comment(comment)
                .url("/questions/" + post.getId())
                .isRead(false)
                .build();
    }

    @Transactional(readOnly = true)
    public NotificationResponses getNotifications(AuthUser authUser) {
        List<NotificationResponse> responses = notificationRepository.findAllByReceiverId(authUser.getUserId()).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        long unreadCount = responses.stream()
                .filter(notification -> !notification.isRead())
                .count();

        return NotificationResponses.of(responses, unreadCount);
    }

    @Transactional
    public void readNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotificationNotFoundException::new);
        notification.read();
    }
}

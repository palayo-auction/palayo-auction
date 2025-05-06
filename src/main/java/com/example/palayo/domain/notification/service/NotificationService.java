package com.example.palayo.domain.notification.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.entity.NotificationHistory;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.notification.repository.NotificationHistoryRepository;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationHistoryRepository historyRepository;

    @Transactional
    public void registerToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

        notificationRepository.findByUser(user).ifPresentOrElse(
                n -> n.updateToken(token),
                () -> notificationRepository.save(Notification.builder()
                        .user(user)
                        .token(token)
                        .build())
        );
    }

    @Transactional
    public void sendNotification(User user, NotificationType type, String title, String body, Map<String, String> data) {
        String token = notificationRepository.findByUser(user)
                .map(Notification::getToken)
                .orElseThrow(() -> new BaseException(ErrorCode.FCM_TOKEN_NOT_FOUND, null));

        Map<String, String> payload = new HashMap<>(data);
        payload.put("title", title);
        payload.put("body", body);

        Message message = Message.builder()
                .setToken(token)
                .putAllData(payload)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            historyRepository.save(NotificationHistory.builder()
                    .user(user)
                    .type(type)
                    .title(title)
                    .body(body)
                    .data(data)
                    .isSent(true)
                    .build());
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL, null);
        }
    }

    @Transactional
    public void reserveNotification(Long userId, NotificationType type, String title, String body, Map<String, String> data, LocalDateTime scheduledAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));
        historyRepository.save(NotificationHistory.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .data(data)
                .scheduledAt(scheduledAt)
                .isSent(false)
                .build());
    }

}

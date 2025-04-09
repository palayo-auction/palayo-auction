package com.example.palayo.domain.notification.scheduler;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.entity.NotificationHistory;
import com.example.palayo.domain.notification.repository.NotificationHistoryRepository;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationHistoryRepository historyRepository;

    @Scheduled(fixedRate = 60000) // 매 1분마다 실행
    @Transactional
    public void sendScheduledNotifications() {
        List<NotificationHistory> pending = historyRepository.findAllByIsSentFalseAndScheduledAtBefore(LocalDateTime.now());

        for (NotificationHistory history : pending) {
            try {
                String token = notificationRepository.findByUser(history.getUser())
                        .map(Notification::getToken)
                        .orElseThrow(() -> new BaseException(ErrorCode.FCM_TOKEN_NOT_FOUND, null));

                Map<String, String> payload = new HashMap<>(history.getData());
                payload.put("title", history.getTitle());
                payload.put("body", history.getBody());

                Message message = Message.builder()
                        .setToken(token)
                        .putAllData(payload)
                        .build();

                FirebaseMessaging.getInstance().send(message);
                history.markAsSent();

            } catch (Exception e) {
                throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL, null);
            }
        }

        historyRepository.saveAll(pending);
    }
}

package com.example.palayo.domain.notification.rabbitMQ;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.config.RabbitMQConfig;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // RabbitMQ 큐에서 메시지를 소비하는 메서드
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(RedisNotification noti) {
        log.info("📥 [RabbitMQ] 알림 수신: {}", noti.getTitle());

        try {
            // 유저 정보 조회
            User user = userRepository.findById(noti.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // FCM 토큰 조회
            String token = notificationRepository.findByUser(user)
                    .map(Notification::getToken)
                    .orElseThrow(() -> new RuntimeException("FCM token not found"));

            // FCM 알림 데이터 준비
            Map<String, String> payload = new HashMap<>(noti.getData());
            payload.put("title", noti.getTitle());
            payload.put("body", noti.getBody());

            // FCM 메시지 빌드
            Message message = Message.builder()
                    .setToken(token) // 유저의 FCM 토큰
                    .putAllData(payload) // 알림 데이터
                    .build();

            // FCM을 통해 알림 전송
            FirebaseMessaging.getInstance().send(message);
            log.info("✅ [FCM] 알림 발송 완료!");

        } catch (Exception e) {
            log.error("❌ [FCM] 알림 발송 실패", e);
        }
    }
}

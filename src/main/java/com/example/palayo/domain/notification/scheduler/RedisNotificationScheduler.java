package com.example.palayo.domain.notification.scheduler;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisNotificationScheduler {

    private final RedisTemplate<String, RedisNotification> redisTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void sendScheduledRedisNotifications() {
        Set<String> keys = redisTemplate.keys("notification:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            RedisNotification noti = redisTemplate.opsForValue().get(key);
            if (noti == null) continue;

            if (noti.getScheduledAt().isBefore(LocalDateTime.now())) {
                try {
                    User user = userRepository.findById(noti.getUserId())
                            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

                    String token = notificationRepository.findByUser(user)
                            .map(Notification::getToken)
                            .orElseThrow(() -> new BaseException(ErrorCode.FCM_TOKEN_NOT_FOUND, null));

                    Message message = Message.builder()
                            .setToken(token)
                            .putAllData(noti.getData())
                            .putData("title", noti.getTitle())
                            .putData("body", noti.getBody())
                            .build();

                    FirebaseMessaging.getInstance().send(message);
                    redisTemplate.delete(key);


                } catch (Exception e) {
                    //throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL,null);
                }
            }
        }
    }
}

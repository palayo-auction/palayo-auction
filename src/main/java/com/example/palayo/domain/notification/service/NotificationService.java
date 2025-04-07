package com.example.palayo.domain.notification.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public String sendDataMessage(String token, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 데이터 메시지 전송 완료: " + response);
            return response;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL,null);
        }
    }

    //token을 등록하기 위해 사용예정
    @Transactional
    public void registerToken(Long userId, String token) {
        //임시 repository를 생성해 작성 후에 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

        Optional<Notification> existing = notificationRepository.findByUser(user);

        if (existing.isPresent()) {
            Notification notification = existing.get();
            notification.setToken(token); // update
            notificationRepository.save(notification);
        } else {
            Notification notification = Notification.builder()
                    .user(user)
                    .token(token)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void sendNotification(User user, String title, String body, Map<String, String> data) {
        Notification notification = notificationRepository.findByUser(user)
                .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_REGISTERED,null));

        Map<String, String> payload = new HashMap<>(data);
        payload.put("title", title);
        payload.put("body", body);

        Message message = Message.builder()
                .setToken(notification.getToken())
                .putAllData(payload)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        //    notificationHistoryRepository.save(NotificationHistory.of(user, title, body));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL,null);
        }
    }

    @Transactional
    public void sendAuctionNotification(Long userId, String title, String body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

        Notification notification = notificationRepository.findByUser(user)
                .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found"));

        String token = notification.getToken();

        Map<String, String> data = Map.of("title", title, "body", body);
        sendDataMessage(token, data);
    }
    //알림 사용 예시
//    ZonedDateTime notifyAt = auctionEndTime.minusMinutes(5);
//auctionJobScheduler.scheduleNotificationJob(
//    userId,
//            "경매 마감 임박!",
//            "참여하신 경매가 5분 후 종료됩니다.",
//    notifyAt
//);

}

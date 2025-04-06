package com.example.palayo.domain.notification.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {
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
}

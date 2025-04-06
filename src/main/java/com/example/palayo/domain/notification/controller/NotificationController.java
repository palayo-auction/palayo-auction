package com.example.palayo.domain.notification.controller;

import com.example.palayo.common.firebase.FirebaseConfig;
import com.example.palayo.domain.notification.dto.request.FirebaseTokenRequest;
import com.example.palayo.domain.notification.service.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.internal.FirebaseService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private Set<String> fcmTokens = new HashSet<>();

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody FirebaseTokenRequest request) {
        try {
            // 🔹 메시지 구성 (notification 말고 data만!)
            Message message = Message.builder()
                    .setToken(request.getToken())
                    .putData("title", request.getTitle())
                    .putData("body", request.getBody())
                    .build();

            // 🔹 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            return ResponseEntity.ok("푸시 알림 전송 성공! : " + response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("푸시 알림 전송 실패: " + e.getMessage());
        }
    }
}

package com.example.palayo.domain.notification.controller;

import com.example.palayo.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body) {
        try {
            String response = notificationService.sendNotification(token, title, body);
            return ResponseEntity.ok("알림 전송 완료: " + response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body("알림 전송 실패: " + e.getMessage());
        }
    }
}

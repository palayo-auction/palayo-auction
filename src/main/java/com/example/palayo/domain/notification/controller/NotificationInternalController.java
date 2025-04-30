package com.example.palayo.domain.notification.controller;

import com.example.palayo.domain.notification.dto.request.SimpleNotificationRequest;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/notifications")  // 🔥 이 경로 꼭 맞춰야 해
@RequiredArgsConstructor
public class NotificationInternalController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> handleNotification(@RequestBody SimpleNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationService.sendNotification(
                user,
                NotificationType.valueOf(request.getType()),  // 문자열 → Enum 변환
                request.getTitle(),
                request.getBody(),
                request.getData()
        );

        return ResponseEntity.ok().build();
    }
}

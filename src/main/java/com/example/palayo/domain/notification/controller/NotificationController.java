package com.example.palayo.domain.notification.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.dto.request.FcmTokenRequest;
import com.example.palayo.domain.notification.dto.request.NotificationRequest;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping("v1/notification/register")
    public ResponseEntity<Void> registerFcmToken(
            @RequestBody FcmTokenRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long userId = authUser.getUserId();
        notificationService.registerToken(userId, request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("v1/notification/send")
    public ResponseEntity<String> sendNotification(
            @RequestBody NotificationRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long userId = authUser.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));

        Map<String, String> data = new HashMap<>();
        data.put("title", request.getTitle());
        data.put("body", request.getBody());

        notificationService.sendNotification(
                user,
                NotificationType.TEST,
                request.getTitle(),
                request.getBody(),
                data);
        return ResponseEntity.ok("알림 전송 성공");
    }

    @PostMapping("v1/notification/test")
    public ResponseEntity<String> testNotification(@AuthenticationPrincipal AuthUser authUser) {
        Long userId = authUser.getUserId();

        Map<String, String> data = new HashMap<>();
        data.put("customKey", "test-value");

        LocalDateTime scheduledAt = LocalDateTime.now().plusMinutes(1);

        notificationService.reserveNotification(
                userId,
                NotificationType.MY_AUCTION_SOON_START,
                "⏰ 테스트 알림",
                "1분 후 이 메시지가 전송",
                data,
                scheduledAt
        );

        return ResponseEntity.ok("알림 예약 완료");
    }
}

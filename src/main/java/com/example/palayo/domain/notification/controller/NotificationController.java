package com.example.palayo.domain.notification.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.notification.dto.request.FcmTokenRequest;
import com.example.palayo.domain.notification.dto.request.NotificationRequest;
import com.example.palayo.domain.notification.dto.response.NotificationResponse;
import com.example.palayo.domain.notification.dto.response.TokenResponse;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping("v1/notification/register")
    public Response<TokenResponse> registerFcmToken(
            @RequestBody FcmTokenRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long userId = authUser.getUserId();
        notificationService.registerToken(userId, request.getToken());

        User user = getUser(userId);

        TokenResponse response = TokenResponse.builder()
                .token(request.getToken())
                .nickname(user.getNickname())
                .build();

        return Response.of(response);
    }

    @PostMapping("v1/notification/send")
    public Response<NotificationResponse> sendNotification(
            @RequestBody NotificationRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        User user = getUser(authUser.getUserId());

        Map<String, String> data = Map.of(
                "title", request.getTitle(),
                "body", request.getBody()
        );

        notificationService.sendNotification(
                user,
                NotificationType.TEST,
                request.getTitle(),
                request.getBody(),
                data
        );

        return Response.of(buildNotificationResponse("알림 전송 성공", "send"));
    }

    @PostMapping("v2/notification/test")
    public Response<NotificationResponse> testNotification(@AuthenticationPrincipal AuthUser authUser) {
        Long userId = authUser.getUserId();

        Map<String, String> data = Map.of("customKey", "test-value");
        LocalDateTime scheduledAt = LocalDateTime.now().plusMinutes(1);

        notificationService.reserveNotification(
                userId,
                NotificationType.MY_AUCTION_SOON_START,
                "테스트 알림",
                "테스트용 알림입니다.",
                data,
                scheduledAt
        );

        return Response.of(buildNotificationResponse("알림 예약 성공", "reserve"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));
    }

    private NotificationResponse buildNotificationResponse(String message, String type) {
        return NotificationResponse.builder()
                .message(message)
                .type(type)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}

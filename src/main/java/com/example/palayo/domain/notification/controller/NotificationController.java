package com.example.palayo.domain.notification.controller;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.dto.request.FcmTokenRequest;
import com.example.palayo.domain.notification.dto.request.NotificationRequest;
import com.example.palayo.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    //User테이블에 저장하게 된다면 토큰 비교해서 중복이면 저장하지 않도록 변경
    @PostMapping("/register")
    public ResponseEntity<String> registerToken(@RequestBody FcmTokenRequest request) {
        System.out.println("토큰 등록 요청됨: " + request.getToken());
        return ResponseEntity.ok("토큰 등록 완료");
        //응답 수정 - 공통 response가 있으면 그걸로 수정 없으면 response 생성후 수정
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("title", request.getTitle());
            data.put("body", request.getBody());

            String response = notificationService.sendDataMessage(request.getToken(), data);
            return ResponseEntity.ok("알림 전송 성공: " + response);
            //응답 수정 - 공통 response가 있으면 그걸로 수정 없으면 response 생성후 수정
        } catch (Exception e) {
            throw new BaseException(ErrorCode.NOTIFICATION_SEND_FAIL,null);
        }
    }
}

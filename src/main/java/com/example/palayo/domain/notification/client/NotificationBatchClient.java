package com.example.palayo.domain.notification.client;

import com.example.palayo.domain.notification.redis.RedisNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationBatchClient {

    private final RestTemplate restTemplate;

    public void scheduleNotification(RedisNotification notification) {
        String url = "http://localhost:8081/api/jobs"; // 배치 서버 주소 (배포 시 수정)

        try {
            restTemplate.postForObject(url, notification, Void.class);
            System.out.println("✅ 배치 서버로 알림 예약 요청 성공");
        } catch (Exception e) {
            System.out.println("❌ 배치 서버 호출 실패: " + e.getMessage());
            throw new RuntimeException("배치 서버 호출 실패", e);
        }
    }
}

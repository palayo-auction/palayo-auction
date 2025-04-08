package com.example.palayo.domain.notification.event;

import com.example.palayo.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("📬 즉시 알림 이벤트 처리 중: userId={}, title={}", event.getUserId(), event.getTitle());
        notificationService.sendImmediateNotification(
                event.getUserId(),
                event.getType(),
                event.getTitle(),
                event.getBody(),
                event.getData()
        );
    }
}

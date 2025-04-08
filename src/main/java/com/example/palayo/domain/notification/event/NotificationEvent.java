package com.example.palayo.domain.notification.event;

import com.example.palayo.domain.notification.entity.NotificationType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private final Long userId;
    private final NotificationType type;
    private final String title;
    private final String body;
    private final Map<String, String> data;

    public NotificationEvent(Object source, Long userId, NotificationType type, String title, String body, Map<String, String> data) {
        super(source);
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.data = data;
    }
}


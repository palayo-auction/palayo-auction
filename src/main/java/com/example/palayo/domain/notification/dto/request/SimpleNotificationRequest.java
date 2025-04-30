package com.example.palayo.domain.notification.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SimpleNotificationRequest {

    private Long userId;
    private String type;
    private String title;
    private String body;
    private Map<String, String> data;
    private LocalDateTime scheduledAt;
}
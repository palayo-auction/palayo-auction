package com.example.palayo.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private String message;
    private String type;
    private String timestamp;
}
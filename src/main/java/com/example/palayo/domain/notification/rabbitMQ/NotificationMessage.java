package com.example.palayo.domain.notification.rabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    private Long userId;
    private String title;
    private String body;
    private Map<String, String> data;
}

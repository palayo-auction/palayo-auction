package com.example.palayo.domain.notification.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisNotification implements Serializable {

    private Long userId;
    private String type;
    private String title;
    private String body;
    private Map<String, String> data;
    private LocalDateTime scheduledAt;

}

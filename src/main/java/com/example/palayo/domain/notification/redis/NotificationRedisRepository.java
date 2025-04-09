package com.example.palayo.domain.notification.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class NotificationRedisRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public NotificationRedisRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveScheduledNotification(RedisNotification notification) {
        try {
            String key = "noti:" + UUID.randomUUID();
            String value = objectMapper.writeValueAsString(notification);
            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    public List<RedisNotification> findDueNotifications() {
        List<RedisNotification> due = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("noti:*");
        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                try {
                    RedisNotification noti = objectMapper.readValue(value, RedisNotification.class);
                    if (noti.getScheduledAt().isBefore(LocalDateTime.now())) {
                        due.add(noti);
                        redisTemplate.delete(key);
                    }
                } catch (Exception e) {
                    // 에러 로그
                }
            }
        }
        return due;
    }
}

package com.example.palayo.config;

import com.example.palayo.domain.notification.redis.RedisNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RedisNotification> redisNotificationTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RedisNotification> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key Serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Value Serializer (Jackson JSON 직렬화)
        Jackson2JsonRedisSerializer<RedisNotification> serializer = new Jackson2JsonRedisSerializer<>(RedisNotification.class);
        serializer.setObjectMapper(new ObjectMapper()
                .activateDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        );
        template.setValueSerializer(serializer);

        return template;
    }
}

package com.example.palayo.config;

//import com.example.palayo.domain.notification.redis.RedisNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // ObjectMapper 설정
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        template.setDefaultSerializer(serializer);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    @Bean
//    public RedisTemplate<String, RedisNotification> redisNotificationRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, RedisNotification> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        objectMapper.activateDefaultTypingAsProperty(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfSubType("com.example.palayo.domain.notification.redis")
//                        .allowIfSubType("java.util")
//                        .build(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                "@Class"
//        );
//
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }

    // 공통 ObjectMapper 설정 메서드
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 Time module 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO8601 포맷 사용
        return objectMapper;
    }

    // RedisTemplate 설정 (Object 타입)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 공통 ObjectMapper 사용
        ObjectMapper objectMapper = createObjectMapper();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setDefaultSerializer(serializer);  // 기본 직렬화 방식
        template.setKeySerializer(new StringRedisSerializer()); // Key는 String으로 직렬화
        template.setValueSerializer(serializer);  // Value는 GenericJackson2JsonRedisSerializer로 직렬화
        template.setHashKeySerializer(new StringRedisSerializer()); // HashKey는 String으로 직렬화
        template.setHashValueSerializer(serializer);  // HashValue는 GenericJackson2JsonRedisSerializer로 직렬화

        template.afterPropertiesSet();
        return template;
    }

//    // RedisTemplate 설정 (RedisNotification 타입)
//    @Bean
//    public RedisTemplate<String, RedisNotification> redisNotificationRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, RedisNotification> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // 공통 ObjectMapper 사용
//        ObjectMapper objectMapper = createObjectMapper();
//
//        // 기본 타입 검증기 설정 (특정 서브타입만 허용)
//        objectMapper.activateDefaultTypingAsProperty(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfSubType("com.example.palayo.domain.notification.redis")  // 패키지 전체 허용
//                        .allowIfSubType("java.util")  // HashMap, ArrayList 등 java.util.* 전부 허용
//                        .build(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                "@Class"
//        );
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        template.setKeySerializer(new StringRedisSerializer()); // Key는 String으로 직렬화
//        template.setValueSerializer(serializer);  // Value는 `RedisNotification` 직렬화
//        template.setHashKeySerializer(new StringRedisSerializer()); // HashKey는 String으로 직렬화
//        template.setHashValueSerializer(serializer);  // HashValue는 `RedisNotification` 직렬화
//
//        template.afterPropertiesSet();
//        return template;
//    }
}
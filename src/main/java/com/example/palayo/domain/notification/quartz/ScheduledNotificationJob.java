package com.example.palayo.domain.notification.quartz;

import com.example.palayo.config.RabbitMQConfig;
import com.example.palayo.domain.notification.rabbitMQ.NotificationMessage;
import com.example.palayo.domain.notification.rabbitMQ.NotificationProducer;
import com.example.palayo.domain.notification.redis.RedisNotification;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
public class ScheduledNotificationJob extends QuartzJobBean {

    private final RedisTemplate<String, RedisNotification> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ScheduledNotificationJob(RedisTemplate<String, RedisNotification> redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String redisKey = jobDataMap.getString("notificationKey");

        if (redisKey == null) {
            log.error("❌ Redis 키가 없습니다!");
            return;
        }

        try {
            RedisNotification noti = redisTemplate.opsForValue().get(redisKey);

            if (noti != null) {
                log.info("✅ Redis에서 알림 가져옴: {}", noti.getTitle());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_NAME,
                        RabbitMQConfig.ROUTING_KEY,
                        noti
                );
                redisTemplate.delete(redisKey);
            } else {
                log.error("❌ Redis에서 알림 못 가져옴: 키={}", redisKey);
            }

        } catch (Exception e) {
            log.error("❌ Quartz 알림 처리 실패", e);
        }
    }
}

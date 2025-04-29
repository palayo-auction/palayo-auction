package com.example.palayo.domain.notification.rabbitMQ;

import com.example.palayo.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(NotificationMessage message) {
        // RabbitMQ로 메시지를 보냄
        rabbitTemplate.convertAndSend("notification.exchange", "notification.routing", message);
    }
}

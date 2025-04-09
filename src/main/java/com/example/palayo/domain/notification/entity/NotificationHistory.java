package com.example.palayo.domain.notification.entity;

import com.example.palayo.common.util.JsonConverter;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String body;

    private LocalDateTime scheduledAt;

    private boolean isSent;

    @Convert(converter = JsonConverter.class)
    private Map<String, String> data;

    private String token;

    public void markAsSent() {
        this.isSent = true;
    }

}

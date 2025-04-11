package com.example.palayo.domain.notification.repository;

import com.example.palayo.domain.notification.entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    List<NotificationHistory> findAllByIsSentFalseAndScheduledAtBefore(LocalDateTime now);
}

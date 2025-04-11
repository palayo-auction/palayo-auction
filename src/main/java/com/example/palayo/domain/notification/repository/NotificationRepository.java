package com.example.palayo.domain.notification.repository;

import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByUser(User user);
}



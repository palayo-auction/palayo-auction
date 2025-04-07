package com.example.palayo.domain.user.entity.log;

import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
public class PointHistories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int amount;

    private PaymentType paymentType;

    @CreatedDate
    private LocalDateTime createdAt;
}

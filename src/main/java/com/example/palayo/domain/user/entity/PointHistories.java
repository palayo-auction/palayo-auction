package com.example.palayo.domain.user.entity;

import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

//TODO 굳이 log로 나눌 필요 없습니다. Depth만 늘어나요
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

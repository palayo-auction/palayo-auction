package com.example.palayo.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private int amount;

    private String method; // 카드, 가상계좌 등

    private String status; // DONE, CANCELED 등

    private String orderName;

    private String customerName;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private Long userId;

    private String nickname;

    private String failReason;
}

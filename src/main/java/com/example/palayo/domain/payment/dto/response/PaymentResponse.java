package com.example.palayo.domain.payment.dto.response;

import com.example.palayo.domain.payment.entity.Payment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse {
    private String orderId;
    private String method;
    private String status;
    private int amount;
    private String failReason;
    private LocalDateTime approvedAt;
    private LocalDateTime requestedAt;
    private String nickname;
    private Long userId;

    public static PaymentResponse of(Payment payment) {
        return new PaymentResponse(
                payment.getOrderId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getFailReason(),
                payment.getApprovedAt(),
                payment.getRequestedAt(),
                payment.getNickname(),
                payment.getUserId()
        );
    }
}

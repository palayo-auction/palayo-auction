package com.example.palayo.domain.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}

package com.example.palayo.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentConfirmResponse {
    private String paymentKey;

    private String orderId;

    private String method;

    private String status;

    private int totalAmount;

    private String orderName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime approvedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime requestedAt;

    private Metadata metadata;

    @Getter
    public static class Metadata {
        private Long userId;
        private String nickname;
        private String customerName;
    }
}


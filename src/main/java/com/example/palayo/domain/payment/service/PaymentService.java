package com.example.palayo.domain.payment.service;

import com.example.palayo.domain.payment.TossPaymentClient;
import com.example.palayo.domain.payment.dto.response.PaymentConfirmResponse;
import com.example.palayo.domain.payment.entity.Payment;
import com.example.palayo.domain.payment.repostiory.PaymentRepository;
import com.example.palayo.domain.pointhistory.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final PointService pointService;

    @Transactional
    public String confirmAndSave(String paymentKey, String orderId, int amount) {

            PaymentConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

            Long userId = response.getMetadata().getUserId();
            String nickname = response.getMetadata().getNickname();

            Payment payment = Payment.builder()
                    .orderId(response.getOrderId())
                    .paymentKey(response.getPaymentKey())
                    .amount(response.getTotalAmount())
                    .method(response.getMethod())
                    .status(response.getStatus())
                    .orderName(response.getOrderName())
                    .customerName(response.getMetadata().getCustomerName())
                    .requestedAt(response.getRequestedAt())
                    .approvedAt(response.getApprovedAt())
                    .userId(userId)
                    .nickname(nickname)
                    .build();

            paymentRepository.save(payment);
            pointService.chargePoints(userId, payment.getAmount());

            return "결제 완료 \n금액: " + payment.getAmount() + "원";
    }

    @Transactional
    public void saveFailedPayment(String orderId, String paymentKey, int amount, String failReason) {
        Payment failedPayment = Payment.builder()
                .orderId(orderId)
                .paymentKey(paymentKey)
                .amount(amount)
                .method(null)
                .status("FAILED")
                .orderName(null)
                .customerName(null)
                .requestedAt(null)
                .approvedAt(null)
                .userId(null)
                .nickname(null)
                .failReason(failReason)
                .build();

        paymentRepository.save(failedPayment);
    }
}


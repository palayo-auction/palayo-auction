package com.example.palayo.domain.payment.service;

import com.example.palayo.domain.payment.entity.Payment;
import com.example.palayo.domain.payment.repostiory.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);

    }
}

package com.example.palayo.domain.payment.service;

import com.example.palayo.domain.payment.dto.request.PaymentConfirmRequest;
import com.example.palayo.domain.payment.entity.Payment;
import com.example.palayo.domain.payment.repostiory.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${payment.secret-key}")
    private String SECRET_KEY;
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public String confirmAndSavePayment(PaymentConfirmRequest request) {
        try {
            String url = "https://api.tosspayments.com/v1/payments/confirm";
            String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedAuth);

            String body = String.format("""
                {
                  "paymentKey": "%s",
                  "orderId": "%s",
                  "amount": %d
                }
                """, request.getPaymentKey(), request.getOrderId(), request.getAmount());

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("결제 실패 ❌: " + json.path("message").asText("Unknown error"));
            }

            Payment payment = Payment.builder()
                    .orderId(json.path("orderId").asText(null))
                    .paymentKey(json.path("paymentKey").asText(null))
                    .amount(json.path("totalAmount").asInt(0))
                    .method(json.path("method").asText(null))
                    .status(json.path("status").asText(null))
                    .orderName(json.path("orderName").asText(null))
                    .customerName(json.path("customerName").asText(null))
                    .requestedAt(json.path("requestedAt").asText(null))
                    .approvedAt(json.path("approvedAt").asText(null))
                    .build();

            paymentRepository.save(payment);
            return "결제 완료 ✅\n결제 수단: " + payment.getMethod() + "\n금액: " + payment.getAmount() + "원";

        } catch (Exception e) {
            throw new RuntimeException("결제 처리 중 오류 발생: " + e.getMessage());
        }
    }
}

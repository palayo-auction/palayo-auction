package com.example.palayo.domain.payment.controller;

import com.example.palayo.domain.payment.dto.request.PaymentConfirmRequest;
import com.example.palayo.domain.payment.entity.Payment;
import com.example.palayo.domain.payment.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${payment.secret-key}")
    private String SECRET_KEY;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentConfirmRequest request) throws Exception {
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
        restTemplate.getMessageConverters();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(responseBody);

        if (response.getStatusCode().is2xxSuccessful()) {
            Payment payment = Payment.builder()
                    .orderId(json.has("orderId") ? json.get("orderId").asText() : null)
                    .paymentKey(json.has("paymentKey") ? json.get("paymentKey").asText() : null)
                    .amount(json.has("totalAmount") ? json.get("totalAmount").asInt() : 0)
                    .method(json.has("method") ? json.get("method").asText() : null)
                    .status(json.has("status") ? json.get("status").asText() : null)
                    .orderName(json.has("orderName") ? json.get("orderName").asText() : null)
                    .customerName(json.has("customerName") ? json.get("customerName").asText() : null)
                    .requestedAt(json.has("requestedAt") ? json.get("requestedAt").asText() : null)
                    .approvedAt(json.has("approvedAt") ? json.get("approvedAt").asText() : null)
                    .build();

            paymentService.save(payment);
            return ResponseEntity.ok(responseBody);
        } else {
            String errorMessage = json.has("message") ? json.get("message").asText() : "Unknown error";
            return ResponseEntity.status(response.getStatusCode())
                    .body("결제 실패 ❌: " + errorMessage);
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam int amount
    ) {
        return ResponseEntity.ok("결제 성공! key=" + paymentKey + ", orderId=" + orderId + ", amount=" + amount);
    }

    @GetMapping("/fail")
    public ResponseEntity<String> paymentFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId
    ) {
        return ResponseEntity.status(400).body("결제 실패 ❌: " + message + " (code: " + code + ")");
    }
}

package com.example.palayo.domain.payment;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.payment.dto.response.PaymentConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    @Value("${payment.secret-key}")
    private String SECRET_KEY;

    private final RestTemplate restTemplate;

    public PaymentConfirmResponse confirmPayment(String paymentKey, String orderId, int amount) {
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
            """, paymentKey, orderId, amount);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<PaymentConfirmResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, PaymentConfirmResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, null);
        }

        return response.getBody();
    }
}


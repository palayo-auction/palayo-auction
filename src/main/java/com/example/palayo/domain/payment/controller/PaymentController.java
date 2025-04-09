package com.example.palayo.domain.payment.controller;

import com.example.palayo.common.response.Response;
import com.example.palayo.domain.payment.dto.request.PaymentConfirmRequest;
import com.example.palayo.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public Response<String> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        String result = paymentService.confirmAndSavePayment(request);
        return Response.of(result);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam int amount
    ) {
        PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(paymentKey, orderId, amount);

        try {
            String result = paymentService.confirmAndSavePayment(confirmRequest);
            return ResponseEntity.ok("✅ 결제 성공 및 저장 완료!\n" + result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 저장 중 오류: " + e.getMessage());
        }
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

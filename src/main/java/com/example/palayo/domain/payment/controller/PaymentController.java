package com.example.palayo.domain.payment.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.payment.dto.request.PaymentConfirmRequest;
import com.example.palayo.domain.payment.dto.response.PaymentResponse;
import com.example.palayo.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public Response<String> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        String result = paymentService.confirmAndSave(request.getPaymentKey(), request.getOrderId(), request.getAmount());
        return Response.of(result);
    }

    @GetMapping("/success")
    public Response<String> paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam int amount
    ) {
        String result = paymentService.confirmAndSave(paymentKey, orderId, amount);
        return Response.of("결제 성공 및 저장 완료\n" + result);
    }

    @GetMapping("/fail")
    public Response<String> paymentFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId
    ) {
        String reason = "결제 실패 : " + message + " (code: " + code + ")";
        paymentService.saveFailedPayment(orderId, null, 0, reason);
        return Response.of(reason);
    }

    @GetMapping
    public Response<List<PaymentResponse>> getPayments(
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable
    ) {
        Page<PaymentResponse> payments = paymentService.getPayments(authUser.getUserId(), pageable);
        return Response.fromPage(payments);
    }
}

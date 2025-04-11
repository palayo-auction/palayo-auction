package com.example.palayo.payment.service;

import com.example.palayo.domain.payment.TossPaymentClient;
import com.example.palayo.domain.payment.dto.response.PaymentConfirmResponse;
import com.example.palayo.domain.payment.entity.Payment;
import com.example.palayo.domain.payment.repostiory.PaymentRepository;
import com.example.palayo.domain.payment.service.PaymentService;
import com.example.palayo.domain.pointhistory.service.PointHistoriesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private TossPaymentClient tossPaymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointHistoriesService pointHistoriesService;

    @Test
    @DisplayName("결제 내역, 포인트 변화 성공 테스트")
    void confirmAndSaveTest() {
        //given
        String paymentKey = "test-key";
        String orderId = "order-123";
        int amount = 10000;
        Long userId = 1L;

        PaymentConfirmResponse.Metadata metadata = PaymentConfirmResponse.Metadata.builder()
                .userId(userId)
                .nickname("테스트")
                .customerName("테스트이름")
                .build();

        PaymentConfirmResponse response = PaymentConfirmResponse.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .method("카드")
                .status("DONE")
                .totalAmount(amount)
                .orderName("테스트 결제")
                .approvedAt(LocalDateTime.now())
                .requestedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();

        given(tossPaymentClient.confirmPayment(paymentKey, orderId, amount)).willReturn(response);

        //when
        String result = paymentService.confirmAndSave(paymentKey, orderId, amount);

        //then
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(pointHistoriesService).chargePoints(metadata.getUserId(), amount);

        assertThat(result).contains("결제 완료").contains("10000원");
    }

    @Test
    @DisplayName("결제 실패시 결제 내역 저장 테스트")
    void saveFailedPayment() {
        // given
        String orderId = "order123";
        String paymentKey = "payKey456";
        int amount = 10000;
        String failReason = "결제 실패 사유";

        // when
        paymentService.saveFailedPayment(orderId, paymentKey, amount, failReason);

        // then
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);    //ArgumentCaptor란? mock 객체에 전달된 값을 검사함. 즉 메서드 호출 인자 확인
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();

        assertThat(saved.getOrderId()).isEqualTo(orderId);
        assertThat(saved.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(saved.getAmount()).isEqualTo(amount);
        assertThat(saved.getStatus()).isEqualTo("FAILED");
        assertThat(saved.getFailReason()).isEqualTo(failReason);
    }
}

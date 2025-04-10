package com.example.palayo.domain.deposithistory.dto;

import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DepositHistoryResponse {

    private Long id;
    private Long auctionId;
    private Long userId;
    private int deposit;
    private DepositStatus status;
    private LocalDateTime createdAt;

    // DepositHistory 엔티티를 DTO로 변환하는 메서드
    public static DepositHistoryResponse fromEntity(DepositHistory depositHistory) {
        return new DepositHistoryResponse(
                depositHistory.getId(),
                depositHistory.getAuction().getId(),
                depositHistory.getUser().getId(),
                depositHistory.getDeposit(),
                depositHistory.getStatus(),
                depositHistory.getCreatedAt()
        );
    }
}

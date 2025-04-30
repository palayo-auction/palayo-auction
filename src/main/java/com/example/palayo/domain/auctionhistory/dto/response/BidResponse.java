package com.example.palayo.domain.auctionhistory.dto.response;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BidResponse {

    private Long auctionId; // 경매 ID
    private int bidPrice;   // 입찰 금액
    private String nickname;
    private LocalDateTime bidTime;

    // private 생성자 (외부에서 직접 생성 불가)
    private BidResponse(Long auctionId, int bidPrice, String nickname, LocalDateTime bidTime) {
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.nickname = nickname;
        this.bidTime = bidTime != null ? bidTime : LocalDateTime.now();
    }

    public static BidResponse of(AuctionHistory auctionHistory, AuthUser authUser) {
        return new BidResponse(
                auctionHistory.getAuction().getId(), // 경매 ID
                auctionHistory.getBidPrice(),         // 입찰 금액
                authUser.getNickname(),
                auctionHistory.getCreatedAt()
        );
    }
}

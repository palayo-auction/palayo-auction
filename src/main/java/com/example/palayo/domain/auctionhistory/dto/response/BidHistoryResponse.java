package com.example.palayo.domain.auctionhistory.dto.response;

import java.time.LocalDateTime;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;

import lombok.Getter;

@Getter
public class BidHistoryResponse {

    private Long auctionId;
    private String nickname;   // 입찰자 닉네임
    private int bidPrice;            // 입찰 금액
    private LocalDateTime bidTime; // 입찰 시간

    // private 생성자 (외부에서 직접 생성 불가)
    private BidHistoryResponse(Long auctionId, String nickname, int bidPrice, LocalDateTime bidTime) {
        this.auctionId = auctionId;
        this.nickname = nickname;
        this.bidPrice = bidPrice;
        this.bidTime = bidTime;
    }

    public static BidHistoryResponse of(AuctionHistory auctionHistory) {
        return new BidHistoryResponse(
                auctionHistory.getAuction().getId(),
                auctionHistory.getBidder().getNickname(), // 입찰자 닉네임
                auctionHistory.getBidPrice(),             // 입찰 금액
                auctionHistory.getCreatedAt()             // 입찰 시간
        );
    }
}

package com.example.palayo.domain.auctionhistory.dto.response;

import java.time.LocalDateTime;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;

import lombok.Getter;

@Getter
public class BidHistoryResponse {

	private String bidderNickname;   // 입찰자 닉네임
	private int bidPrice;            // 입찰 금액
	private LocalDateTime createdAt; // 입찰 시간

	// private 생성자 (외부에서 직접 생성 불가)
	private BidHistoryResponse(String bidderNickname, int bidPrice, LocalDateTime createdAt) {
		this.bidderNickname = bidderNickname;
		this.bidPrice = bidPrice;
		this.createdAt = createdAt;
	}

	public static BidHistoryResponse of(AuctionHistory auctionHistory) {
		return new BidHistoryResponse(
			auctionHistory.getBidder().getNickname(), // 입찰자 닉네임
			auctionHistory.getBidPrice(),             // 입찰 금액
			auctionHistory.getCreatedAt()             // 입찰 시간
		);
	}
}

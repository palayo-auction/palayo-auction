package com.example.palayo.domain.auctionhistory.dto.response;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;

import lombok.Getter;

@Getter
public class BidResponse {

	private Long auctionId; // 경매 ID
	private int bidPrice;   // 입찰 금액

	// private 생성자 (외부에서 직접 생성 불가)
	private BidResponse(Long auctionId, int bidPrice) {
		this.auctionId = auctionId;
		this.bidPrice = bidPrice;
	}

	public static BidResponse of(AuctionHistory auctionHistory) {
		return new BidResponse(
			auctionHistory.getAuction().getId(), // 경매 ID
			auctionHistory.getBidPrice()         // 입찰 금액
		);
	}
}

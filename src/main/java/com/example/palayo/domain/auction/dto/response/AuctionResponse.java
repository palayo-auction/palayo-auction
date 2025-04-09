package com.example.palayo.domain.auction.dto.response;

import com.example.palayo.domain.auction.entity.Auction;

public record AuctionResponse(Long auctionId, String status) {

	public static AuctionResponse of(Auction auction) {
		return new AuctionResponse(
			auction.getId(),           // 경매 ID
			auction.getStatus().name() // 경매 상태 (Enum -> String)
		);
	}
}


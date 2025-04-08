package com.example.palayo.domain.auction.dto.response;

import com.example.palayo.domain.auction.entity.Auction;

import lombok.Getter;

@Getter
public class AuctionResponse {

	private Long auctionId; // 경매 ID
	private String status;  // 경매 상태

	public AuctionResponse(Long auctionId, String status) {
		this.auctionId = auctionId;
		this.status = status;
	}

	public static AuctionResponse of(Auction auction) {
		return new AuctionResponse(
			auction.getId(),           // 경매 ID
			auction.getStatus().name() // 경매 상태 (Enum -> String)
		);
	}
}

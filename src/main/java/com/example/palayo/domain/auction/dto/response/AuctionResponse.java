package com.example.palayo.domain.auction.dto.response;

import com.example.palayo.domain.auction.entity.Auction;

import lombok.Getter;

/**
 * @param auctionId 경매 ID
 * @param status    경매 상태
 */
//TODO auctionId / status 필드는 생성자에서 값이 설정되고 이후에 바뀌지 않습니다 따라서 final 혹은 record 스타일로 사용가능해요
@Getter
public record AuctionResponse(Long auctionId, String status) {

	public static AuctionResponse of(Auction auction) {
		return new AuctionResponse(
				auction.getId(),           // 경매 ID
				auction.getStatus().name() // 경매 상태 (Enum -> String)
		);
	}
}


//@Getter
//public class AuctionResponse {
//
//	private final Long auctionId; // 경매 ID
//	private final String status;  // 경매 상태
//
//	public AuctionResponse(Long auctionId, String status) {
//		this.auctionId = auctionId;
//		this.status = status;
//	}
//
//	public static AuctionResponse of(Auction auction) {
//		return new AuctionResponse(
//				auction.getId(),           // 경매 ID
//				auction.getStatus().name() // 경매 상태 (Enum -> String)
//		);
//	}
//}
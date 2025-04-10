package com.example.palayo.domain.auction.dto.response;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.itemimage.entity.ItemImage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionListResponse {

	private Long auctionId;        // 경매 ID
	private String itemName;       // 상품명
	private String itemImageUrl;   // 대표 상품 이미지 URL
	private String auctionStatus;  // 경매 상태
	private int currentPrice; 	   // 현재 최고 입찰가
	private String remainingTime;  // 남은 시간

	// N+1 최적화 예정 (Item, ItemImages 연관 조회)
	public static AuctionListResponse of(Auction auction, String remainingTime) {
		return AuctionListResponse.builder()
			.auctionId(auction.getId())
			.itemName(auction.getItem().getName())
			.itemImageUrl( // 대표 상품 이미지 URL (imageIndex = 0)
				auction.getItem().getItemImages().stream()
					.filter(image -> image.getImageIndex() == 0)
					.findFirst()
					.map(ItemImage::getImageUrl)
					.orElse(null)
			)
			.auctionStatus(auction.getStatus().name())
			.currentPrice(auction.getCurrentPrice())
			.remainingTime(remainingTime)
			.build();
	}
}
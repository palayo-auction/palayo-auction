package com.example.palayo.domain.auction.dto.response;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 응답에서 제외
@Getter
@Builder
public class AuctionListResponse {

	private Long auctionId;        // 경매 ID
	private String itemName;       // 상품 이름
	private String itemImageUrl;   // 대표 상품 이미지 URL
	private String auctionStatus;  // 경매 상태
	private int currentPrice;      // 현재 최고 입찰가
	private Integer myBidPrice;    // 내 최고 입찰가 (nullable) - 공용 DTO로 상황에 따라 값 숨김을 위해 Integer 사용
	private Boolean isWinner;      // 낙찰 여부 (nullable)

	private String remainingTime;  // 남은 시간

	// N+1 최적화 예정 (Item, ItemImages 연관 조회)
	public static AuctionListResponse of(
		Auction auction,
		String remainingTime,
		Integer myBidPrice,
		Boolean isWinner
	) {
		return AuctionListResponse.builder()
			.auctionId(auction.getId())
			.itemName(auction.getItem().getName())

			// 대표 상품 이미지 URL (imageIndex = 0)
			.itemImageUrl(
				auction.getItem().getItemImages().stream()
					.filter(image -> image.getImageIndex() == 0)
					.findFirst()
					.map(ItemImage::getImageUrl)
					.orElse(null)
			)

			.auctionStatus(auction.getStatus().name())
			.currentPrice(auction.getCurrentPrice())
			.myBidPrice(myBidPrice)
			.isWinner(isWinner)
			.remainingTime(remainingTime)
			.build();
	}
}
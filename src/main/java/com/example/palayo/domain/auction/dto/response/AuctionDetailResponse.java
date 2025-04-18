package com.example.palayo.domain.auction.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL) // null 필드는 응답에서 제외
@Getter
@Builder
public class AuctionDetailResponse {

	private Long auctionId;               // 경매 ID
	private String sellerNickname;        // 판매자 닉네임
	private String winningBidderNickname; // 낙찰자 닉네임 (nullable)

	private String itemName;              // 상품명
	private String itemContent;           // 상품 설명
	private List<String> itemImageUrls;   // 상품 이미지 URL 리스트 (imageIndex 오름차순 정렬)

	private String auctionStatus;         // 경매 상태
	private int startingPrice;            // 시작가
	private int buyoutPrice;              // 즉시 낙찰가
	private int currentPrice;             // 현재 최고 입찰가
	private Integer myBidPrice;           // 내가 입찰한 최고 금액 (nullable) - 공용 DTO로 상황에 따라 값 숨김을 위해 Integer 사용
	private Boolean isWinner;             // 낙찰자인지 여부 (nullable)

	private LocalDateTime startedAt;      // 경매 시작 일시
	private LocalDateTime expiredAt;      // 경매 종료 일시
	private String remainingTime;         // 남은 시간

	// N+1 최적화 예정 (Item, ItemImages, Seller 연관 조회)
	public static AuctionDetailResponse of(
		Auction auction,
		String remainingTime,
		String winningBidderNickname,
		Integer myBidPrice,
		Boolean isWinner
	) {
		return AuctionDetailResponse.builder()
			.auctionId(auction.getId())
			.sellerNickname(auction.getItem().getSeller().getNickname())
			.winningBidderNickname(winningBidderNickname)

			.itemName(auction.getItem().getName())
			.itemContent(auction.getItem().getContent())

			// 상품 이미지 URL 리스트 (imageIndex 오름차순 정렬)
			.itemImageUrls(
				auction.getItem().getItemImages().stream()
					.sorted(Comparator.comparingInt(ItemImage::getImageIndex))
					.map(ItemImage::getImageUrl)
					.toList()
			)

			.auctionStatus(auction.getStatus().name())
			.startingPrice(auction.getStartingPrice())
			.buyoutPrice(auction.getBuyoutPrice())
			.currentPrice(auction.getCurrentPrice())
			.myBidPrice(myBidPrice)
			.isWinner(isWinner)

			.startedAt(auction.getStartedAt())
			.expiredAt(auction.getExpiredAt())
			.remainingTime(remainingTime)
			.build();
	}
}

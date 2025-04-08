package com.example.palayo.domain.auction.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.example.palayo.domain.auction.entity.Auction;

import com.example.palayo.domain.itemimage.entity.ItemImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyAuctionDetailResponse {

	private Long auctionId;               // 경매 ID
	private String sellerNickname;        // 판매자 닉네임
	private String winningBidderNickname; // 낙찰자 닉네임 (nullable)
	private String itemName;              // 상품명
	private String itemContent;           // 상품 설명
	private List<String> itemImageUrls;   // 상품 이미지 URL 리스트
	private String auctionStatus;         // 경매 상태
	private Integer currentPrice;         // 현재 최고 입찰가
	private int startingPrice;            // 시작가
	private int buyoutPrice;              // 즉시 낙찰가
	private LocalDateTime startedAt;      // 경매 시작 일시
	private LocalDateTime expiredAt;      // 경매 종료 일시
	private String remainingTime;         // 남은 시간

	public static MyAuctionDetailResponse of(Auction auction, String remainingTime, String winningBidderNickname) {
		return MyAuctionDetailResponse.builder()
			.auctionId(auction.getId())
			.sellerNickname(auction.getItem().getSeller().getNickname())
			.winningBidderNickname(winningBidderNickname)
			.itemName(auction.getItem().getName())
			.itemContent(auction.getItem().getContent())
			.itemImageUrls( // 상품 이미지 URL 리스트 (imageIndex 오름차순 정렬)
				auction.getItem().getItemImages().stream()
					.sorted(Comparator.comparingInt(ItemImage::getImageIndex))
					.map(ItemImage::getImageUrl)
					.toList()
			)
			.auctionStatus(auction.getStatus().name())
			.currentPrice(auction.getCurrentPrice())
			.startingPrice(auction.getStartingPrice())
			.buyoutPrice(auction.getBuyoutPrice())
			.startedAt(auction.getStartedAt())
			.expiredAt(auction.getExpiredAt())
			.remainingTime(remainingTime)
			.build();
	}
}

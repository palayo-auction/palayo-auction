package com.example.palayo.domain.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateAuctionRequest {

	@NotNull(message = "상품 ID는 필수입니다.")
	private Long itemId; // 상품 ID

	@NotNull(message = "시작가는 필수입니다.")
	private int startingPrice; // 경매 시작가

	@NotNull(message = "즉시 낙찰가는 필수입니다.")
	private int buyoutPrice; // 즉시 낙찰가

	@NotNull(message = "입찰 단위는 필수입니다.")
	private Integer bidIncrement; // 입찰 단위

	@NotNull(message = "경매 시작 시간은 필수입니다.")
	private LocalDateTime startedAt; // 경매 시작 일시

	@NotNull(message = "경매 종료 시간은 필수입니다.")
	private LocalDateTime expiredAt; // 경매 종료 일시
}

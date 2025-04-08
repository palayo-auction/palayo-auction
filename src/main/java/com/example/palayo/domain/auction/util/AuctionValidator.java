package com.example.palayo.domain.auction.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

// 경매 생성 시 상품, 소유자, 요청 값 유효성을 검증하는 유틸리티 클래스
@Component
@RequiredArgsConstructor
public class AuctionValidator {

	private final ItemRepository itemRepository;
	private final AuctionRepository auctionRepository;

	// 경매 생성 요청에 대한 종합 검증
	public Item validateAuctionCreation(CreateAuctionRequest request, AuthUser authUser) {

		// 상품 존재 여부 검증
		Item item = itemRepository.findById(request.getItemId())
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ITEM_OWNER, "itemId"));

		// 요청자가 상품 소유자인지 검증
		if (!item.getSeller().getId().equals(authUser.getUserId())) {
			throw new BaseException(ErrorCode.INVALID_ITEM_OWNER, "itemId");
		}

		// 상품이 이미 경매에 등록되어 있는지 검증
		if (auctionRepository.existsByItemIdAndStatusIn(
			request.getItemId(), List.of(AuctionStatus.READY, AuctionStatus.ACTIVE))) {
			throw new BaseException(ErrorCode.ITEM_ALREADY_ON_AUCTION, "itemId");
		}

		// 요청 데이터 유효성 검증
		LocalDateTime now = LocalDateTime.now();

		if (request.getStartedAt().isBefore(now)) { // 시작 시간이 과거이면 예외
			throw new BaseException(ErrorCode.INVALID_START_TIME, "startedAt");
		}

		if (request.getExpiredAt().isBefore(request.getStartedAt())) { // 종료 시간이 시작 시간보다 빠르면 예외
			throw new BaseException(ErrorCode.INVALID_END_TIME, "expiredAt");
		}

		if (request.getStartingPrice() < 100) { // 시작가가 100 미만이면 예외
			throw new BaseException(ErrorCode.INVALID_MINIMUM_PRICE, "startingPrice");
		}

		if (request.getStartingPrice() >= request.getBuyoutPrice()) { // 시작가가 즉시 낙찰가 이상이면 예외
			throw new BaseException(ErrorCode.INVALID_STARTING_PRICE, "startingPrice");
		}

		if (!isValidBidIncrement(request.getBidIncrement())) { // 입찰 단위가 허용 범위가 아니면 예외
			throw new BaseException(ErrorCode.INVALID_BID_INCREMENT, "bidIncrement");
		}

		if (request.getBidIncrement() >= request.getStartingPrice()) { // 입찰 단위가 시작가 이상이면 예외
			throw new BaseException(ErrorCode.BID_INCREMENT_TOO_HIGH, "bidIncrement");
		}

		return item; // 모든 검증을 통과한 상품 반환
	}

	// 입찰 단위가 허용된 값(100, 1000, 10000, 100000, 1000000)인지 검증
	private boolean isValidBidIncrement(int bidIncrement) {
		return bidIncrement == 100 || bidIncrement == 1000 || bidIncrement == 10000
			|| bidIncrement == 100000 || bidIncrement == 1000000;
	}
}

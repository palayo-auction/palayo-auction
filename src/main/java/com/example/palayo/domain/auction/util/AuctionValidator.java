package com.example.palayo.domain.auction.util;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.util.ItemValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 경매 생성 시 유효성 검증을 담당하는 유틸리티
@Component
@RequiredArgsConstructor
public class AuctionValidator {

	private final ItemValidator itemValidator;
	private final AuctionRepository auctionRepository;

	// 경매 생성 요청 전체를 검증하고 유효한 상품을 반환합니다.
	public Item validateAuctionCreation(CreateAuctionRequest request, AuthUser authUser) {

		// 1. 상품 존재 및 소유자 검증
		Item item = itemValidator.getValidItem(request.getItemId());

		if (!item.getSeller().getId().equals(authUser.getUserId())) {
			throw new BaseException(ErrorCode.INVALID_ITEM_OWNER, "itemId");
		}

		// 2. 해당 상품이 이미 경매에 등록된 상태인지 확인
		if (auctionRepository.existsByItemIdAndStatusIn(
			request.getItemId(), List.of(AuctionStatus.READY, AuctionStatus.ACTIVE))) {
			throw new BaseException(ErrorCode.ITEM_ALREADY_ON_AUCTION, "itemId");
		}

		LocalDateTime now = LocalDateTime.now();
		boolean isInstant = Boolean.TRUE.equals(request.getIsInstantStart());

		// 3. 시간 조건 검증
		if (isInstant) {
			// 즉시 시작 경매는 최소 30분 이후 종료여야 함
			if (request.getExpiredAt() == null || !request.getExpiredAt().isAfter(now.plusMinutes(1))) {
				throw new BaseException(ErrorCode.INVALID_DURATION, "expiredAt");
			}
		} else {
			// 예약 경매는 시작 시간이 현재 이후, 종료 시간이 시작 이후 30분 이상이어야 함
			if (request.getStartedAt() == null || request.getStartedAt().isBefore(now)) {
				throw new BaseException(ErrorCode.INVALID_START_TIME, "startedAt");
			}
			if (request.getExpiredAt() == null || !request.getExpiredAt()
				.isAfter(request.getStartedAt().plusMinutes(1))) {
				throw new BaseException(ErrorCode.INVALID_DURATION, "expiredAt");
			}
		}

		// 4. 가격 조건 검증
		if (request.getStartingPrice() < 100) {
			throw new BaseException(ErrorCode.INVALID_MINIMUM_PRICE, "startingPrice");
		}
		if (request.getStartingPrice() >= request.getBuyoutPrice()) {
			throw new BaseException(ErrorCode.INVALID_STARTING_PRICE, "startingPrice");
		}

		// 5. 입찰 단위 검증
		if (!isValidBidIncrement(request.getBidIncrement())) {
			throw new BaseException(ErrorCode.INVALID_BID_INCREMENT, "bidIncrement");
		}
		if (request.getBidIncrement() >= request.getStartingPrice()) {
			throw new BaseException(ErrorCode.BID_INCREMENT_TOO_HIGH, "bidIncrement");
		}

		// 검증 통과 → 유효한 Item 반환
		return item;
	}

	// 허용된 입찰 단위인지 확인합니다.
	private boolean isValidBidIncrement(int bidIncrement) {
		return bidIncrement == 100 || bidIncrement == 1000 || bidIncrement == 10000
			|| bidIncrement == 100000 || bidIncrement == 1000000;
	}
}

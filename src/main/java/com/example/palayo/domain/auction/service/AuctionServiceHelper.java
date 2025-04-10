package com.example.palayo.domain.auction.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.auction.util.AuctionTimeUtils;

import lombok.RequiredArgsConstructor;

// 경매 상태 변경, 낙찰 처리, 권한 및 삭제 검증을 담당하는 헬퍼 클래스
@Component
@RequiredArgsConstructor
public class AuctionServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;

	// 경매 상태 업데이트 (현재 시간과 경매 진행 상황에 따라 상태 변경)
	public boolean updateStatus(Auction auction) {
		LocalDateTime now = LocalDateTime.now();

		if (isBuyoutPriceReached(auction)) { // 즉시 낙찰가 도달 시 낙찰 확정 처리
			updateToSuccess(auction);
			return true;
		}

		if (AuctionTimeUtils.isBeforeStart(now, auction)) { // 경매 시작 전 → READY 상태로 변경
			auction.markAsReady();
			return true;
		}

		if (AuctionTimeUtils.isDuringAuction(now, auction)) { // 경매 진행 중 → ACTIVE 상태로 변경
			auction.markAsActive();
			return true;
		}

		if (AuctionTimeUtils.isAfterEnd(now, auction)) { // 경매 종료 → 낙찰 또는 유찰 처리
			updateAfterExpired(auction);
			return true;
		}

		return false;
	}

	// 낙찰자 선정 (종료되었거나 즉시 낙찰가 도달 시 낙찰자 확정)
	public boolean assignWinningBidder(Auction auction) {
		if (!hasBids(auction)) { // 입찰 기록이 없으면 낙찰자 없음
			return false;
		}

		if (auction.getWinningBidder() == null) { // 최고 입찰 기록 조회 (가격 높은 순, 입찰 시간 빠른 순)

			AuctionHistory topBid = auctionHistoryRepository.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId())
				.orElseThrow(() -> new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId"));

			auction.setWinningBidder(topBid.getBidder()); // 최고 입찰자를 경매의 낙찰자로 설정
		}

		if (isBuyoutPriceReached(auction)) { // 즉시 낙찰가 도달 시 낙찰 확정 처리
			updateToSuccess(auction);
			return true;
		}

		if (AuctionTimeUtils.isAfterEnd(LocalDateTime.now(), auction)) { // 경매 종료 시 낙찰자 확정 또는 유찰 처리
			updateAfterExpired(auction);
			return true;
		}

		return false;
	}

	// 경매 소유자 검증 (본인이 등록한 경매인지 확인)
	public void validateOwnership(AuthUser authUser, Auction auction) {
		if (!auction.getItem().getSeller().getId().equals(authUser.getUserId())) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 경매 삭제 가능 여부 검증 (READY, SUCCESS, FAILED 상태만 삭제 허용)
	public void validateDeletableAuction(Auction auction) {
		if (auction.getStatus() == AuctionStatus.DELETED) { // 이미 삭제된 경매
			throw new BaseException(ErrorCode.ALREADY_DELETED_AUCTION, "auctionId");
		}

		if (!(auction.getStatus() == AuctionStatus.READY
			|| auction.getStatus() == AuctionStatus.SUCCESS
			|| auction.getStatus() == AuctionStatus.FAILED)) { // 진행 중이면 삭제 불가
			throw new BaseException(ErrorCode.CANNOT_DELETE_ACTIVE_AUCTION, "auctionId");
		}
	}

	// 경매에 입찰 기록 존재 여부 확인
	private boolean hasBids(Auction auction) {
		return auctionHistoryRepository.existsByAuctionId(auction.getId());
	}

	// 현재 최고 입찰가가 즉시 낙찰가 이상인지 확인
	private boolean isBuyoutPriceReached(Auction auction) {
		if (!hasBids(auction)) {
			return false;
		}
		return auction.getCurrentPrice() >= auction.getBuyoutPrice();
	}

	// 경매를 SUCCESS 상태로 변경
	private void updateToSuccess(Auction auction) {
		if (auction.getWinningBidder() == null) { // 낙찰자가 없으면 예외 발생
			throw new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId");
		}
		auction.markAsSuccess(auction.getWinningBidder());
	}

	// 경매 종료 후 낙찰 성공 또는 유찰 처리
	private void updateAfterExpired(Auction auction) {
		if (auction.getWinningBidder() != null) { // 낙찰자 존재 시 SUCCESS 처리
			auction.markAsSuccess(auction.getWinningBidder());
		} else { // 낙찰자 없음 → FAILED 처리
			auction.markAsFailed();
		}
	}
}
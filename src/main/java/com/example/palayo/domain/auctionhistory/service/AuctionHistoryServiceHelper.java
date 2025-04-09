package com.example.palayo.domain.auctionhistory.service;

import org.springframework.stereotype.Component;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

// 입찰 관련 검증, 조회, 보조 기능을 담당하는 헬퍼 클래스
@Component
@RequiredArgsConstructor
public class AuctionHistoryServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final DepositHistoryService depositHistoryService;

	// 본인이 등록한 경매에 입찰하는지 검증
	public void validateNotOwner(Auction auction, User bidder) {
		if (auction.getItem().getSeller().getId().equals(bidder.getId())) {
			throw new BaseException(ErrorCode.CANNOT_BID_OWN_AUCTION, "auctionId");
		}
	}

	// 입찰 금액 유효성 검증 (현재가 + 입찰단위 이상이어야 함)
	public void validateBidPrice(Auction auction, int bidPrice) {
		int minValidPrice = auction.getCurrentPrice() + auction.getBidIncrement();
		if (bidPrice < minValidPrice) {
			throw new BaseException(ErrorCode.BID_PRICE_TOO_LOW, "bidPrice");
		}
	}

	// 포인트 초과 여부 검증 (입찰가 단독 및 누적 합산 초과 체크)
	public void checkPointLimit(User bidder, int newBidPrice) {
		Long totalBidAmount = auctionHistoryRepository.sumBidPricesByUserId(bidder.getId());
		if (totalBidAmount == null) {
			totalBidAmount = 0L;
		}

		int userPointAmount = bidder.getPointAmount();

		if (newBidPrice > userPointAmount) {
			throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "bidPrice");
		}

		if (totalBidAmount + newBidPrice > userPointAmount) {
			throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "bidPrice");
		}
	}

	// 보증금 생성 (기존에 납부한 보증금이 없는 경우에만)
	public void createDepositIfNotExists(Auction auction, User bidder) {
		boolean alreadyDeposited = depositHistoryService.existsByAuctionAndUser(auction.getId(), bidder.getId());
		if (!alreadyDeposited) {
			int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);
			depositHistoryService.createDepositHistory(bidder.getId(), auction.getId(), depositAmount);
		}
	}

	// 즉시 낙찰가 이상 입찰 여부 확인
	public boolean isBuyoutPriceReached(Auction auction, int bidPrice) {
		return bidPrice >= auction.getBuyoutPrice();
	}

	// 경매 참여 여부 검증 (입찰 기록 존재 여부 확인)
	public void validateParticipation(Long auctionId, Long userId) {
		boolean participated = auctionHistoryRepository.existsByAuctionIdAndBidderId(auctionId, userId);
		if (!participated) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 낙찰자 닉네임 조회 (성공 또는 삭제된 경매에서만 반환)
	public String getWinningBidderNickname(Auction auction) {
		if (auction.getStatus() == AuctionStatus.SUCCESS ||
			(auction.getStatus() == AuctionStatus.DELETED && auction.getWinningBidder() != null)) {
			return auction.getWinningBidder().getNickname();
		}
		return null;
	}
}

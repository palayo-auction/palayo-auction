package com.example.palayo.domain.auctionhistory.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.pointhistory.service.PointHistoriesService;
import com.example.palayo.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuctionHistoryServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final DepositHistoryService depositHistoryService;
	private final PointHistoriesService pointHistoriesService;

	// 사용자가 본인 경매에 입찰하려고 하는지 검증 (본인 경매 입찰 불가)
	public void validateNotOwner(Auction auction, User bidder) {
		if (auction.getItem().getSeller().getId().equals(bidder.getId())) {
			throw new BaseException(ErrorCode.CANNOT_BID_OWN_AUCTION, "auctionId");
		}
	}

	// 입찰 가격이 유효한지 검증 (현재 가격 + 최소 입찰 단위 이상이어야 함)
	public void validateBidPrice(Auction auction, int bidPrice) {
		int minValidPrice = auction.getCurrentPrice() + auction.getBidIncrement();
		if (bidPrice < minValidPrice) {
			throw new BaseException(ErrorCode.BID_PRICE_TOO_LOW, "bidPrice");
		}
	}

	// 사용자의 포인트가 부족하지 않은지 검증
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

	// 기존에 보증금 납부 기록이 없으면 보증금 생성
	public void createDepositIfNotExists(Auction auction, User bidder) {
		boolean alreadyDeposited = depositHistoryService.existsByAuctionAndUser(auction.getId(), bidder.getId());
		if (!alreadyDeposited) {
			int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);

			// 보증금 기록 (PENDING 상태)
			depositHistoryService.createDepositHistory(bidder.getId(), auction.getId(), depositAmount);

			// 포인트 감소 및 포인트 기록 추가
			pointHistoriesService.decreasePoints(bidder.getId(), depositAmount);
		}
	}

	// 즉시 낙찰가에 도달했는지 여부를 확인
	public boolean isBuyoutPriceReached(Auction auction, int bidPrice) {
		return bidPrice >= auction.getBuyoutPrice();
	}

	// 사용자가 해당 경매에 참여했는지 검증
	public void validateParticipation(Long auctionId, Long userId) {
		boolean participated = auctionHistoryRepository.existsByAuctionIdAndBidderId(auctionId, userId);
		if (!participated) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 경매의 낙찰자 닉네임을 조회하는 메서드
	public String getWinningBidderNickname(Auction auction) {
		if (auction.getStatus() == AuctionStatus.SUCCESS ||
			(auction.getStatus() == AuctionStatus.DELETED && auction.getWinningBidder() != null)) {
			return auction.getWinningBidder().getNickname();
		}
		return null;
	}

	// 낙찰 성공 시 보증금 처리 및 포인트 차감
	public void handleAuctionSuccess(Auction auction, User winner, int finalBidPrice) {
		depositHistoryService.useDeposit(auction.getId(), winner.getId());
		int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);
		int additionalCharge = finalBidPrice - depositAmount;
		if (additionalCharge > 0) {
			pointHistoriesService.decreasePoints(winner.getId(), additionalCharge);
		}
	}

	// 경매 실패자 보증금과 포인트 환불 처리
	public void refundFailedBidders(Auction auction) {
		List<AuctionHistory> bidHistories = auctionHistoryRepository.findByAuctionId(auction.getId());
		List<User> failedBidders = bidHistories.stream()
			.map(AuctionHistory::getBidder)
			.filter(bidder -> !bidder.getId().equals(auction.getWinningBidder().getId()))
			.distinct()
			.collect(Collectors.toList());

		for (User failedBidder : failedBidders) {
			depositHistoryService.refundDeposit(auction.getId(), failedBidder.getId());
			int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);
			pointHistoriesService.refundPoints(failedBidder.getId(), depositAmount);
		}
	}
}

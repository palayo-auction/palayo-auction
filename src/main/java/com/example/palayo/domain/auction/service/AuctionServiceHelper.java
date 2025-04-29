package com.example.palayo.domain.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.util.AuctionTimeUtils;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.pointhistory.mongo.service.PointHistoryService;
import com.example.palayo.domain.pointhistory.service.PointHistoriesService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PointType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final DepositHistoryRepository depositHistoryRepository;
	private final DepositHistoryService depositHistoryService;
	private final PointHistoriesService pointHistoriesService;
	private final PointHistoryService pointHistoryService;
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;

	// ----- public 메서드 -----

	// 경매 상태를 현재 시간 기준으로 업데이트합니다.
	public boolean updateStatus(Auction auction) {
		LocalDateTime now = LocalDateTime.now();

		if (isBuyoutPriceReached(auction)) {
			updateToSuccess(auction);
			return true;
		}
		if (AuctionTimeUtils.isBeforeStart(now, auction)) {
			auction.markAsReady();
			return true;
		}
		if (AuctionTimeUtils.isDuringAuction(now, auction)) {
			auction.markAsActive();
			return true;
		}
		if (AuctionTimeUtils.isAfterEnd(now, auction)) {
			updateAfterExpired(auction);
			return true;
		}
		return false;
	}

	// 경매 종료 시 낙찰자를 지정합니다.
	public boolean assignWinningBidder(Auction auction) {
		if (auction.getStatus() == AuctionStatus.SUCCESS) {
			return false;
		}
		if (!hasBids(auction)) {
			sendBidFailNotifications(auction);
			return false;
		}
		if (auction.getWinningBidder() == null) {
			AuctionHistory topBid = auctionHistoryRepository.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(
				auction.getId()).orElseThrow(() -> new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId"));

			auction.setWinningBidder(topBid.getBidder());
			sendBidSuccessNotification(auction);
		}
		if (isBuyoutPriceReached(auction) || AuctionTimeUtils.isAfterEnd(LocalDateTime.now(), auction)) {
			updateToSuccess(auction);
			return true;
		}
		sendBidFailNotifications(auction);
		return false;
	}

	// 즉시 낙찰 조건을 만족하는 경우, 최고 입찰자를 낙찰자로 지정하고 후처리를 수행합니다.
	public void checkAndHandleAuctionAfterBid(Auction auction) {
		if (auction.getStatus() != AuctionStatus.ACTIVE)
			return;
		if (!isBuyoutPriceReached(auction))
			return;

		// 최고 입찰 내역 조회
		AuctionHistory topBid = auctionHistoryRepository
			.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId"));

		// 최고 입찰자를 낙찰자로 설정
		User winningBidder = topBid.getBidder();
		int winningPrice = topBid.getBidPrice();

		auction.setWinningBidder(winningBidder);
		auction.markAsSuccess(winningBidder, LocalDateTime.now());

		// 포인트 차감 및 환불 처리도 topBid 기준으로
		handleAuctionSuccess(auction, winningBidder, winningPrice);
		refundFailedBidders(auction);
	}

	// 요청한 사용자가 경매 소유자인지 검증합니다.
	public void validateOwnership(AuthUser authUser, Auction auction) {
		if (!auction.getItem().getSeller().getId().equals(authUser.getUserId())) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 경매가 삭제 가능한 상태인지 검증합니다.
	public void validateDeletableAuction(Auction auction) {
		if (auction.getStatus() == AuctionStatus.DELETED) {
			throw new BaseException(ErrorCode.ALREADY_DELETED_AUCTION, "auctionId");
		}
		if (!(auction.getStatus() == AuctionStatus.READY
			|| auction.getStatus() == AuctionStatus.SUCCESS
			|| auction.getStatus() == AuctionStatus.FAILED)) {
			throw new BaseException(ErrorCode.CANNOT_DELETE_ACTIVE_AUCTION, "auctionId");
		}
	}

	// 낙찰 정보(닉네임, 낙찰 시각)를 반환합니다.
	public WinningInfo getWinningInfoIfPresent(Auction auction) {
		if ((auction.getStatus() == AuctionStatus.SUCCESS || auction.getStatus() == AuctionStatus.DELETED)
			&& auction.getWinningBidder() != null) {
			return new WinningInfo(
				auction.getWinningBidder().getNickname(),
				auction.getSuccessAt()
			);
		}
		return null;
	}

	// 낙찰자 정보 record
	public static record WinningInfo(String nickname, LocalDateTime successAt) {
	}

	// ----- private 메서드 -----

	// 해당 경매에 입찰 기록이 있는지 확인합니다.
	private boolean hasBids(Auction auction) {
		return auctionHistoryRepository.existsByAuctionId(auction.getId());
	}

	// 현재 가격이 즉시구매가에 도달했는지 확인합니다.
	private boolean isBuyoutPriceReached(Auction auction) {
		if (!hasBids(auction)) {
			return false;
		}
		return auction.getCurrentPrice() >= auction.getBuyoutPrice();
	}

	// 낙찰 처리를 수행하고 후처리를 진행합니다.
	private void updateToSuccess(Auction auction) {
		if (auction.getStatus() == AuctionStatus.SUCCESS || auction.getSuccessAt() != null) {
			return;
		}

		// 실제 최고 입찰자 기준으로 낙찰 처리
		AuctionHistory topBid = auctionHistoryRepository
			.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId"));

		User winningBidder = topBid.getBidder();
		int winningPrice = topBid.getBidPrice();

		auction.setWinningBidder(winningBidder);

		LocalDateTime successTime = isBuyoutPriceReached(auction)
			? LocalDateTime.now()
			: auction.getExpiredAt();

		auction.markAsSuccess(winningBidder, successTime);

		handleAuctionSuccess(auction, winningBidder, winningPrice);
		refundFailedBidders(auction);
	}

	// 경매 종료 후 낙찰 또는 유찰 상태로 처리합니다.
	private void updateAfterExpired(Auction auction) {
		if (auction.getWinningBidder() != null) {
			updateToSuccess(auction);
		} else {
			auction.markAsFailed();
		}
	}

	// 낙찰 처리: 보증금 사용, 포인트 정산
	private void handleAuctionSuccess(Auction auction, User winner, int finalBidPrice) {
		boolean isDepositAlreadyUsed = depositHistoryRepository
			.findByAuctionAndUser(auction, winner)
			.map(d -> d.getStatus() == DepositStatus.USED)
			.orElse(false);

		if (!isDepositAlreadyUsed) {
			depositHistoryService.useDeposit(auction.getId(), winner.getId());
		}

		int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);
		int additionalCharge = finalBidPrice - depositAmount;

		if (!isDepositAlreadyUsed && additionalCharge > 0) {
			pointHistoriesService.updatePoints(winner.getId(), -additionalCharge, PointType.DECREASE);
			pointHistoryService.updatePointHistory(winner.getId(), -additionalCharge, PointType.DECREASE);
		}

		User seller = auction.getItem().getSeller();
		pointHistoriesService.updatePoints(seller.getId(), finalBidPrice, PointType.INCREASE);
		pointHistoryService.updatePointHistory(seller.getId(), finalBidPrice, PointType.INCREASE);
	}

	// 낙찰 실패자 보증금/포인트 환불 처리
	private void refundFailedBidders(Auction auction) {
		List<AuctionHistory> bidHistories = auctionHistoryRepository.findByAuctionId(auction.getId());

		List<User> failedBidders = bidHistories.stream()
			.map(AuctionHistory::getBidder)
			.filter(bidder -> !bidder.getId().equals(auction.getWinningBidder().getId()))
			.distinct()
			.toList();

		for (User failedBidder : failedBidders) {
			depositHistoryService.refundDeposit(auction.getId(), failedBidder.getId());
			int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);
			pointHistoriesService.updatePoints(failedBidder.getId(), depositAmount, PointType.REFUNDED);
			pointHistoryService.updatePointHistory(failedBidder.getId(), depositAmount, PointType.REFUNDED);
		}
	}

	// ----- 알림 전송 관련 메서드 -----

	// 낙찰 성공 알림을 전송합니다.
	private void sendBidSuccessNotification(Auction auction) {
		RedisNotification winNotice = redisNotificationFactory.bidWin(auction.getWinningBidder(), auction);
		notificationService.saveNotification(winNotice);
	}

	// 입찰 실패자들에게 유찰 알림을 전송합니다.
	private void sendBidFailNotifications(Auction auction) {
		List<User> participants = auctionHistoryRepository.findAllBiddersByAuctionId(auction.getId());
		for (User user : participants) {
			RedisNotification failNotice = redisNotificationFactory.bidFail(user, auction);
			notificationService.saveNotification(failNotice);
		}
	}
}
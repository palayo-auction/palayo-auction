package com.example.palayo.domain.auctionhistory.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.pointhistory.mongo.service.PointHistoryService;
import com.example.palayo.domain.pointhistory.service.PointHistoriesService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionHistoryServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final DepositHistoryRepository depositHistoryRepository;
	private final UserRepository userRepository;
	private final DepositHistoryService depositHistoryService;
	private final PointHistoriesService pointHistoriesService;
	private final PointHistoryService pointHistoryService;
	private final AuctionRepository auctionRepository;
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;
	private final RedissonClient redissonClient;

	// 본인 경매 입찰 불가
	public void validateNotOwner(Auction auction, User bidder) {
		if (auction.getItem().getSeller().getId().equals(bidder.getId())) {
			throw new BaseException(ErrorCode.CANNOT_BID_OWN_AUCTION, "auctionId");
		}
	}

	// 최소 입찰 단위 미만이면 예외
	public void validateBidPrice(Auction auction, int bidPrice) {
		int minValidPrice = auction.getCurrentPrice() + auction.getBidIncrement();
		if (bidPrice < minValidPrice) {
			throw new BaseException(ErrorCode.BID_PRICE_TOO_LOW, "bidPrice");
		}
	}

	// Redis 포인트 조회 및 초기화
	private long getUserPointFromRedis(Long userId) {
		String redisKey = "user:point:" + userId;
		RAtomicLong redisPoint = redissonClient.getAtomicLong(redisKey);

		if (!redisPoint.isExists()) {
			Long dbPoint = userRepository.findById(userId)
				.map(User::getPointAmount)
				.map(i -> (long) i)  // 메서드 참조 대신 람다로 명시적 캐스팅
				.orElse(0L);

			redisPoint.set(dbPoint);
			return dbPoint;
		}

		return redisPoint.get();
	}

	// 포인트 한도 초과 여부 검증
	public void checkPointLimit(User bidder, Auction auction, int newBidPrice) {
		long userPointAmount = getUserPointFromRedis(bidder.getId()); // DB 대신 Redis 기준으로
		int deposit = (int)Math.ceil(auction.getStartingPrice() * 0.1);

		// 즉시 낙찰가 이상일 경우: 보증금 제외한 금액만 비교
		if (newBidPrice >= auction.getBuyoutPrice()) {
			int finalCharge = newBidPrice - deposit;
			if (finalCharge > userPointAmount) {
				throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "bidPrice");
			}
			return;
		}

		long totalMaxBidAmount = auctionHistoryRepository.sumMaxBidPricesByUserIdOnActiveAuctions(bidder.getId());
		int totalDeposits = depositHistoryRepository.sumDepositsByUserId(bidder.getId());
		long occupiedPoints = totalMaxBidAmount - totalDeposits;

		if (occupiedPoints + newBidPrice > userPointAmount) {
			throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "bidPrice");
		}
	}

	// 입찰 시 보증금 납부 처리 (없을 경우만)
	public void createDepositIfNotExists(Auction auction, User bidder) {
		boolean alreadyDeposited = depositHistoryService.existsByAuctionAndUser(auction.getId(), bidder.getId());
		if (!alreadyDeposited) {
			int depositAmount = (int)Math.ceil(auction.getStartingPrice() * 0.1);

			depositHistoryService.createDepositHistory(bidder.getId(), auction.getId(), depositAmount);
			pointHistoriesService.updatePoints(bidder.getId(), depositAmount, PointType.DECREASE);
			pointHistoryService.updatePointHistory(bidder.getId(), depositAmount, PointType.DECREASE);
		}
	}

	// 즉시낙찰가 도달 여부
	public boolean isBuyoutPriceReached(Auction auction, int bidPrice) {
		return bidPrice >= auction.getBuyoutPrice();
	}

	// 해당 경매 참여 여부 검증
	public void validateParticipation(Long auctionId, Long userId) {
		boolean participated = auctionHistoryRepository.existsByAuctionIdAndBidderId(auctionId, userId);
		if (!participated) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 낙찰자 정보 반환
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

	// 낙찰자 정보 불변 record
	public static record WinningInfo(String nickname, LocalDateTime successAt) {
	}

	// 내가 해당 경매에서 입찰한 최고 금액 조회
	public Integer getMyHighestBid(Long auctionId, Long userId) {
		return auctionHistoryRepository
			.findTopByAuctionIdAndBidderIdOrderByBidPriceDescCreatedAtDesc(auctionId, userId)
			.map(AuctionHistory::getBidPrice)
			.orElse(null);
	}

	// 경매 종료 후 낙찰자인지 여부 (진행 중이면 null)
	public Boolean isWinner(Auction auction, Long userId) {
		if (auction.getStatus() == AuctionStatus.ACTIVE) {
			return null;
		}
		return auction.getWinningBidder() != null &&
			auction.getWinningBidder().getId().equals(userId);
	}

	// 이전 입찰자에게 입찰 실패 알림 전송
	public void sendOutbidNotification(User previousTopBidder, Auction auction) {
		RedisNotification notification = redisNotificationFactory.bidOutbid(previousTopBidder, auction);
		notificationService.saveNotification(notification);
	}
}
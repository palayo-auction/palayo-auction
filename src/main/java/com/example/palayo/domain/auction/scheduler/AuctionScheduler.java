package com.example.palayo.domain.auction.scheduler;

import java.util.List;

import com.example.palayo.domain.auction.util.AuctionTimeUtils;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auction.service.AuctionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

	private final AuctionRepository auctionRepository;
	private final AuctionService auctionService;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;

	// 1초마다 경매 상태 및 낙찰자 갱신 (변경된 경우에만 save) - 최적화 예정
	@Scheduled(fixedRate = 1000)
	@Transactional
	public void updateAuctionStatuses() {

		// READY 또는 ACTIVE 상태인 경매 전체 조회
		List<Auction> auctions = auctionRepository.findAllByStatusIn(
			List.of(AuctionStatus.READY, AuctionStatus.ACTIVE)
		);

		for (Auction auction : auctions) {
			// 경매 상태 자동 변경 (READY → ACTIVE → SUCCESS/FAILED)
			boolean statusUpdated = auctionService.updateAuctionStatus(auction);

			// 낙찰자 지정 (즉시낙찰, 종료낙찰)
			boolean winnerAssigned = auctionService.assignWinningBidder(auction);

			// 경매 종료 5분 전 알림 전송
			if (auction.getStatus() == AuctionStatus.ACTIVE &&
				AuctionTimeUtils.isAboutToExpireInFiveMinutes(auction)) {

				// 최고 입찰자 한 명 조회
				auctionHistoryRepository.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId())
					.ifPresent(topBid -> {
						// 최고 입찰자에게 경매 종료 임박 알림 발송
						RedisNotification notification =
							redisNotificationFactory.bidEnd(topBid.getBidder(), auction);
						notificationService.saveNotification(notification);
					});
			}

			// 상태 변경 또는 낙찰자 지정이 발생한 경우에만 save 호출
			if (statusUpdated || winnerAssigned) {
				auctionRepository.save(auction);
			}
		}
	}
}




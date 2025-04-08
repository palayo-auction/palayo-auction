package com.example.palayo.domain.auction.scheduler;

import java.util.List;

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

	// 1초마다 경매 상태 및 낙찰자 갱신 (변경된 경우에만 save) - 최적화 예정
	@Scheduled(fixedRate = 1000)
	@Transactional
	public void updateAuctionStatuses() {

		// READY 또는 ACTIVE 상태인 경매 전체 조회
		List<Auction> auctions = auctionRepository.findAllByStatusIn(
			List.of(AuctionStatus.READY, AuctionStatus.ACTIVE)
		);

		for (Auction auction : auctions) {
			boolean statusUpdated = auctionService.updateAuctionStatus(
				auction);  // 경매 상태 자동 변경 (READY → ACTIVE → SUCCESS/FAILED)
			boolean winnerAssigned = auctionService.assignWinningBidder(auction); // 낙찰자 지정 (즉시낙찰, 종료낙찰)

			// 상태 변경 또는 낙찰자 지정이 발생한 경우에만 save 호출
			if (statusUpdated || winnerAssigned) {
				auctionRepository.save(auction);
			}
		}
	}
}




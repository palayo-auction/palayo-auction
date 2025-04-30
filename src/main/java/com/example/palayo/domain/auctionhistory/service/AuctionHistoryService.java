package com.example.palayo.domain.auctionhistory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auction.service.AuctionServiceHelper;
import com.example.palayo.domain.auction.util.TimeFormatter;
import com.example.palayo.domain.auctionhistory.dto.request.CreateBidRequest;
import com.example.palayo.domain.auctionhistory.dto.response.BidHistoryResponse;
import com.example.palayo.domain.auctionhistory.dto.response.BidResponse;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionHistoryService {

	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final UserRepository userRepository;
	private final DepositHistoryService depositHistoryService;
	private final AuctionHistoryServiceHelper auctionHistoryServiceHelper;
	private final AuctionServiceHelper auctionServiceHelper;
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;
	private final RedissonClient redissonClient; // Redis Test

	// 입찰을 생성합니다.
	@Transactional
	public BidResponse createBid(AuthUser authUser, Long auctionId, CreateBidRequest request) {
		Auction auction = findActiveAuctionById(auctionId);
		User bidder = findUserById(authUser.getUserId());

		auctionHistoryServiceHelper.validateNotOwner(auction, bidder);
		auctionHistoryServiceHelper.validateBidPrice(auction, request.getBidPrice());
		auctionHistoryServiceHelper.checkPointLimit(bidder, auction, request.getBidPrice());
		auctionHistoryServiceHelper.createDepositIfNotExists(auction, bidder);

		Optional<AuctionHistory> previousTopBidOpt = auctionHistoryRepository
			.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId());

		AuctionHistory auctionHistory = AuctionHistory.of(auction, bidder, request.getBidPrice());
		auctionHistoryRepository.save(auctionHistory);

		auction.updateCurrentPrice(request.getBidPrice());
		auctionRepository.save(auction);

		auctionServiceHelper.checkAndHandleAuctionAfterBid(auction);

		if (previousTopBidOpt.isPresent()) {
			User previousTopBidder = previousTopBidOpt.get().getBidder();
			if (!previousTopBidder.getId().equals(bidder.getId())) {
				auctionHistoryServiceHelper.sendOutbidNotification(previousTopBidder, auction);
			}
		}

		return BidResponse.of(auctionHistory, authUser);
	}

	// // 입찰을 생성합니다. (Redisson Lock으로 동시성 제어)
	// @Transactional
	// public BidResponse createBid(AuthUser authUser, Long auctionId, CreateBidRequest request) {
	// 	RLock lock = redissonClient.getLock("auction:bid:" + auctionId);
	// 	boolean locked = false;
	//
	// 	try {
	// 		locked = lock.tryLock(3, 5, TimeUnit.SECONDS); // 3초 안에 락 획득 시도, 5초 유지
	//
	// 		if (!locked) {
	// 			throw new BaseException(ErrorCode.BID_CONFLICT, "동시 입찰 충돌");
	// 		}
	//
	// 		Auction auction = findActiveAuctionById(auctionId);
	// 		User bidder = findUserById(authUser.getUserId());
	//
	// 		auctionHistoryServiceHelper.validateNotOwner(auction, bidder);
	// 		auctionHistoryServiceHelper.validateBidPrice(auction, request.getBidPrice());
	// 		auctionHistoryServiceHelper.checkPointLimit(bidder, auction, request.getBidPrice());
	// 		auctionHistoryServiceHelper.createDepositIfNotExists(auction, bidder);
	//
	// 		Optional<AuctionHistory> previousTopBidOpt = auctionHistoryRepository
	// 			.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId());
	//
	// 		AuctionHistory auctionHistory = AuctionHistory.of(auction, bidder, request.getBidPrice());
	// 		auctionHistoryRepository.save(auctionHistory);
	//
	// 		auction.updateCurrentPrice(request.getBidPrice());
	// 		auctionRepository.save(auction);
	//
	// 		// bidder, bidPrice를 넘기지 않고 auction만 넘긴다
	// 		auctionServiceHelper.checkAndHandleAuctionAfterBid(auction);
	//
	// 		if (previousTopBidOpt.isPresent()) {
	// 			User previousTopBidder = previousTopBidOpt.get().getBidder();
	// 			if (!previousTopBidder.getId().equals(bidder.getId())) {
	// 				auctionHistoryServiceHelper.sendOutbidNotification(previousTopBidder, auction);
	// 			}
	// 		}
	//
	// 		return BidResponse.of(auctionHistory);
	//
	// 	} catch (InterruptedException e) {
	// 		Thread.currentThread().interrupt();
	// 		throw new BaseException(ErrorCode.BID_LOCK_FAILED, "락 획득 실패");
	// 	} finally {
	// 		if (locked && lock.isHeldByCurrentThread()) {
	// 			lock.unlock();
	// 		}
	// 	}
	// }

	// 특정 경매의 입찰 내역을 조회합니다.
	@Transactional(readOnly = true)
	public Page<BidHistoryResponse> getAuctionBidHistories(Long auctionId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<AuctionHistory> histories = auctionHistoryRepository.findByAuctionId(auctionId, pageable);
		return histories.map(BidHistoryResponse::of);
	}

	// 내가 참여한 경매 목록을 조회합니다.
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getParticipatedAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Long userId = authUser.getUserId();

		List<Long> participatedAuctionIds = auctionHistoryRepository.findDistinctAuctionIdsByBidderId(userId);
		if (participatedAuctionIds.isEmpty()) {
			return Page.empty(pageable);
		}

		Page<Auction> auctions = auctionRepository.findAllByIdInAndStatusIn(
			participatedAuctionIds,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();

		return auctions.map(auction -> {
			Integer myBidPrice = auctionHistoryServiceHelper.getMyHighestBid(auction.getId(), userId);
			Boolean isWinner = auctionHistoryServiceHelper.isWinner(auction, userId);
			return AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), myBidPrice,
				isWinner);
		});
	}

	// 내가 참여한 특정 경매를 상세 조회합니다.
	@Transactional(readOnly = true)
	public AuctionDetailResponse getParticipatedAuctionDetail(AuthUser authUser, Long auctionId) {
		Auction auction = auctionRepository.findByIdAndStatusIn(
			auctionId,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED)
		).orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		Long userId = authUser.getUserId();

		auctionHistoryServiceHelper.validateParticipation(auctionId, userId);

		AuctionHistoryServiceHelper.WinningInfo winningInfo = auctionHistoryServiceHelper.getWinningInfoIfPresent(
			auction);
		String winningBidderNickname = winningInfo != null ? winningInfo.nickname() : null;
		LocalDateTime successAt = winningInfo != null ? winningInfo.successAt() : null;

		Integer myBidPrice = auctionHistoryServiceHelper.getMyHighestBid(auctionId, userId);
		Boolean isWinner = auctionHistoryServiceHelper.isWinner(auction, userId);

		LocalDateTime now = LocalDateTime.now();

		return AuctionDetailResponse.of(
			auction, TimeFormatter.formatRemainingTime(now, auction), winningBidderNickname, myBidPrice, isWinner,
			successAt
		);
	}

	// ID로 ACTIVE 상태의 경매를 조회합니다.
	private Auction findActiveAuctionById(Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
		if (auction.getStatus() != AuctionStatus.ACTIVE) {
			throw new BaseException(ErrorCode.INVALID_AUCTION_STATUS, "auctionId");
		}
		return auction;
	}

	// 사용자 ID로 사용자 정보를 조회합니다.
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));
	}
}
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
import com.example.palayo.domain.auction.util.TimeFormatter;
import com.example.palayo.domain.auctionhistory.dto.request.CreateBidRequest;
import com.example.palayo.domain.auctionhistory.dto.response.BidHistoryResponse;
import com.example.palayo.domain.auctionhistory.dto.response.BidResponse;
import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;

	// 사용자가 경매에 입찰할 때 호출하는 메서드
	@Transactional
	public BidResponse createBid(AuthUser authUser, Long auctionId, CreateBidRequest request) {
		Auction auction = findActiveAuctionById(auctionId);
		User bidder = findUserById(authUser.getUserId());

		// 입찰자가 상품 주인인지 검증 (주인이면 입찰 불가)
		auctionHistoryServiceHelper.validateNotOwner(auction, bidder);
		// 입찰 가격이 유효한지 검증
		auctionHistoryServiceHelper.validateBidPrice(auction, request.getBidPrice());
		// 사용자의 포인트가 충분한지 체크
		auctionHistoryServiceHelper.checkPointLimit(bidder, request.getBidPrice());
		// 입찰 보증금이 존재하지 않으면 생성
		auctionHistoryServiceHelper.createDepositIfNotExists(auction, bidder);

		// 입찰 전 최고 입찰자 찾기 (알림용)
		Optional<AuctionHistory> previousTopBidOpt = auctionHistoryRepository.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(
			auction.getId());

		// 입찰 내역 생성 및 저장
		AuctionHistory auctionHistory = AuctionHistory.of(auction, bidder, request.getBidPrice());
		auctionHistoryRepository.save(auctionHistory);

		// 현재 경매 최고가 업데이트
		auction.updateCurrentPrice(request.getBidPrice());

		// 입찰 가격이 즉시낙찰가에 도달했는지 체크 후 경매 성공 처리
		if (auctionHistoryServiceHelper.isBuyoutPriceReached(auction, request.getBidPrice())) {
			auction.markAsSuccess(bidder);
			auctionHistoryServiceHelper.handleAuctionSuccess(auction, bidder, request.getBidPrice());
			auctionHistoryServiceHelper.refundFailedBidders(auction);
		}

		// 알림 보내기: 최고 입찰자가 변경된 경우
		if (previousTopBidOpt.isPresent()) {
			User previousTopBidder = previousTopBidOpt.get().getBidder();
			if (!previousTopBidder.getId().equals(bidder.getId())) {
				RedisNotification notification = redisNotificationFactory.bidOutbid(previousTopBidder, auction);
				notificationService.saveNotification(notification);
			}
		}

		return BidResponse.of(auctionHistory);
	}

	// 특정 경매에 대한 입찰 기록을 조회하는 메서드
	@Transactional(readOnly = true)
	public Page<BidHistoryResponse> getAuctionBidHistories(Long auctionId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// 해당 경매 ID로 입찰 기록을 페이징 조회
		Page<AuctionHistory> histories = auctionHistoryRepository.findByAuctionId(auctionId, pageable);

		// BidHistoryResponse로 변환하여 반환
		return histories.map(BidHistoryResponse::of);
	}

	// 사용자가 참여한 경매 목록을 조회하는 메서드
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getParticipatedAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// 사용자가 입찰한 경매 ID 목록 조회
		List<Long> participatedAuctionIds = auctionHistoryRepository.findDistinctAuctionIdsByBidderId(
			authUser.getUserId());

		// 참여한 경매가 없으면 빈 페이지 반환
		if (participatedAuctionIds.isEmpty()) {
			return Page.empty(pageable);
		}

		// 참여한 경매들 중 특정 상태(ACTIVE, SUCCESS, DELETED)만 조회
		Page<Auction> auctions = auctionRepository.findAllByIdInAndStatusIn(
			participatedAuctionIds,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED),
			pageable
		);

		// AuctionListResponse로 변환하여 반환
		LocalDateTime now = LocalDateTime.now();
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction)));
	}

	// 사용자가 참여한 특정 경매 상세정보를 조회하는 메서드
	@Transactional(readOnly = true)
	public AuctionDetailResponse getParticipatedAuctionDetail(AuthUser authUser, Long auctionId) {
		// 경매 조회 (특정 상태만 허용)
		Auction auction = auctionRepository.findByIdAndStatusIn(
			auctionId,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED)
		).orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// 사용자가 이 경매에 참여했는지 검증
		auctionHistoryServiceHelper.validateParticipation(auctionId, authUser.getUserId());

		// 낙찰자의 닉네임 가져오기
		String winningBidderNickname = auctionHistoryServiceHelper.getWinningBidderNickname(auction);

		// AuctionDetailResponse로 변환하여 반환
		LocalDateTime now = LocalDateTime.now();
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction),
			winningBidderNickname);
	}

	// 경매 ID로 ACTIVE 상태의 경매를 조회하는 메서드 (단순 조회용)
	private Auction findActiveAuctionById(Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
		if (auction.getStatus() != AuctionStatus.ACTIVE) {
			throw new BaseException(ErrorCode.INVALID_AUCTION_STATUS, "auctionId");
		}
		return auction;
	}

	// 사용자 ID로 사용자 조회하는 메서드 (단순 조회용)
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));
	}
}
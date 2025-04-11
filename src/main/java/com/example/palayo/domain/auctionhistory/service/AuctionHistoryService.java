package com.example.palayo.domain.auctionhistory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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

	// 입찰 생성
	@Transactional
	public BidResponse createBid(AuthUser authUser, Long auctionId, CreateBidRequest request) {

		// 경매 ID로 ACTIVE 상태의 경매 조회
		Auction auction = findActiveAuctionById(auctionId);

		// 사용자 조회
		User bidder = findUserById(authUser.getUserId());

		// 본인이 등록한 경매에 입찰하는지 검증
		auctionHistoryServiceHelper.validateNotOwner(auction, bidder);

		// 입찰 금액 유효성 검증
		auctionHistoryServiceHelper.validateBidPrice(auction, request.getBidPrice());

		// 포인트 초과 여부 검증
		auctionHistoryServiceHelper.checkPointLimit(bidder, request.getBidPrice());

		// 보증금 생성 (최초 입찰 시)
		auctionHistoryServiceHelper.createDepositIfNotExists(auction, bidder);

		// 입찰 기록 저장
		AuctionHistory auctionHistory = AuctionHistory.of(auction, bidder, request.getBidPrice());
		auctionHistoryRepository.save(auctionHistory);

		// 경매 현재 최고 입찰가 갱신
		auction.updateCurrentPrice(request.getBidPrice());

		// 즉시 낙찰가 도달 시 경매 성공 처리
		if (auctionHistoryServiceHelper.isBuyoutPriceReached(auction, request.getBidPrice())) {
			auction.markAsSuccess(bidder);
		}

		Optional<AuctionHistory> previousTopBidOpt = auctionHistoryRepository
				.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId());

		if (previousTopBidOpt.isPresent()) {
			User previousTopBidder = previousTopBidOpt.get().getBidder();

			if (!previousTopBidder.getId().equals(bidder.getId())) {
				RedisNotification notification = redisNotificationFactory.bidOutbid(previousTopBidder, auction);
				notificationService.saveNotification(notification);
			}
		}

		// 입찰 결과 반환
		return BidResponse.of(auctionHistory);
	}

	// 특정 경매의 입찰 내역 조회
	@Transactional(readOnly = true)
	public Page<BidHistoryResponse> getAuctionBidHistories(Long auctionId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// 경매 ID로 입찰 기록 페이징 조회
		Page<AuctionHistory> histories = auctionHistoryRepository.findByAuctionId(auctionId, pageable);

		// BidHistoryResponse로 변환하여 반환
		return histories.map(BidHistoryResponse::of);
	}

	// 내가 참여한 경매 목록 조회
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getParticipatedAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// 로그인 사용자가 입찰한 경매 ID 목록 조회
		List<Long> participatedAuctionIds = auctionHistoryRepository.findDistinctAuctionIdsByBidderId(authUser.getUserId());

		// 참여한 경매가 없는 경우 빈 페이지 반환
		if (participatedAuctionIds.isEmpty()) {
			return Page.empty(pageable);
		}

		// 참여한 경매 ID 목록 중 ACTIVE, SUCCESS, DELETED 상태인 경매 페이징 조회
		Page<Auction> auctions = auctionRepository.findAllByIdInAndStatusIn(
			participatedAuctionIds,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED),
			pageable
		);

		// AuctionListResponse로 변환하여 반환
		LocalDateTime now = LocalDateTime.now();
		return auctions.map(auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction)));
	}

	// 내가 참여한 경매 단건 조회
	@Transactional(readOnly = true)
	public AuctionDetailResponse getParticipatedAuctionDetail(AuthUser authUser, Long auctionId) {

		// 경매 조회 (ACTIVE, SUCCESS, DELETED 상태만)
		Auction auction = auctionRepository.findByIdAndStatusIn(
			auctionId,
			List.of(AuctionStatus.ACTIVE, AuctionStatus.SUCCESS, AuctionStatus.DELETED)
		).orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// 경매 참여 여부 검증
		auctionHistoryServiceHelper.validateParticipation(auctionId, authUser.getUserId());

		// 낙찰자 닉네임 조회
		String winningBidderNickname = auctionHistoryServiceHelper.getWinningBidderNickname(auction);

		// AuctionDetailResponse로 변환하여 반환
		LocalDateTime now = LocalDateTime.now();
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), winningBidderNickname);
	}

	// 경매 ID로 ACTIVE 상태의 경매 조회 (없으면 예외 던짐)
	// 검증이나 비즈니스 로직이 아닌 단순 조회용 메서드라 AuctionHistoryService 내부에 둠
	private Auction findActiveAuctionById(Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
		if (auction.getStatus() != AuctionStatus.ACTIVE) {
			throw new BaseException(ErrorCode.INVALID_AUCTION_STATUS, "auctionId");
		}
		return auction;
	}

	// 유저 ID로 유저 조회 (없으면 예외 던짐)
	// 검증이나 비즈니스 로직이 아닌 단순 조회용 메서드라 AuctionHistoryService 내부에 둠
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));
	}
}

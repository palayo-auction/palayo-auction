package com.example.palayo.domain.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auction.dto.response.AuctionResponse;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auction.util.TimeFormatter;
import com.example.palayo.domain.auction.util.AuctionValidator;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.auctionhistory.repository.AuctionHistoryRepository;
import com.example.palayo.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final ItemRepository itemRepository;
	private final AuctionServiceHelper auctionServiceHelper;
	private final AuctionValidator auctionValidator;
	private final NotificationService notificationService;
	private final RedisNotificationFactory redisNotificationFactory;

	// 경매를 생성합니다.
	@Transactional
	public AuctionResponse saveAuction(AuthUser authUser, CreateAuctionRequest request) {
		Item item = auctionValidator.validateAuctionCreation(request, authUser);

		LocalDateTime startedAt;
		LocalDateTime expiredAt;
		AuctionStatus status;

		if (Boolean.TRUE.equals(request.getIsInstantStart())) {
			startedAt = LocalDateTime.now();
			expiredAt = request.getExpiredAt();
			status = AuctionStatus.ACTIVE;
		} else {
			startedAt = request.getStartedAt();
			expiredAt = request.getExpiredAt();
			status = AuctionStatus.READY;
		}

		Auction auction = Auction.of(
			item,
			request.getStartingPrice(),
			request.getBuyoutPrice(),
			request.getBidIncrement(),
			startedAt,
			expiredAt
		);
		auction.updateCurrentPrice(request.getStartingPrice());

		if (status == AuctionStatus.ACTIVE) {
			auction.markAsActive();
		} else {
			auction.markAsReady();
		}

		Auction savedAuction = auctionRepository.save(auction);
		reserveMyAuctionNotification(savedAuction);

		return AuctionResponse.of(savedAuction);
	}

	// 경매 상태를 갱신합니다.
	@Transactional
	public boolean updateAuctionStatus(Auction auction) {
		return auctionServiceHelper.updateStatus(auction);
	}

	// 최고 입찰자를 낙찰자로 지정합니다.
	@Transactional
	public boolean assignWinningBidder(Auction auction) {
		return auctionServiceHelper.assignWinningBidder(auction);
	}

	// 진행 중인 경매를 조회합니다.
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getAuctions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<Auction> auctions = auctionRepository.findAllByStatusIn(
			List.of(AuctionStatus.READY, AuctionStatus.ACTIVE),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), null, null)
		);
	}

	// 특정 경매를 조회합니다.
	@Transactional(readOnly = true)
	public AuctionDetailResponse getAuction(Long auctionId) {
		Auction auction = findAuctionByIdAndStatus(auctionId, List.of(AuctionStatus.READY, AuctionStatus.ACTIVE));
		LocalDateTime now = LocalDateTime.now();
		return AuctionDetailResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), null, null, null,
			null);
	}

	// 내가 등록한 모든 경매를 조회합니다.
	@Transactional(readOnly = true)
	public Page<AuctionListResponse> getMyAuctions(AuthUser authUser, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<Auction> auctions = auctionRepository.findAllByItemSellerIdAndStatusIn(
			authUser.getUserId(),
			List.of(
				AuctionStatus.READY,
				AuctionStatus.ACTIVE,
				AuctionStatus.SUCCESS,
				AuctionStatus.FAILED
			),
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		return auctions.map(
			auction -> AuctionListResponse.of(auction, TimeFormatter.formatRemainingTime(now, auction), null, null)
		);
	}

	// 내가 등록한 특정 경매를 상세 조회합니다.
	@Transactional(readOnly = true)
	public AuctionDetailResponse getMyAuction(AuthUser authUser, Long auctionId) {
		Auction auction = findAuctionByIdAndStatus(
			auctionId,
			List.of(
				AuctionStatus.READY,
				AuctionStatus.ACTIVE,
				AuctionStatus.SUCCESS,
				AuctionStatus.FAILED
			)
		);

		auctionServiceHelper.validateOwnership(authUser, auction);
		AuctionServiceHelper.WinningInfo winningInfo = auctionServiceHelper.getWinningInfoIfPresent(auction);
		LocalDateTime now = LocalDateTime.now();

		String winningNickname = winningInfo != null ? winningInfo.nickname() : null;
		LocalDateTime successAt = winningInfo != null ? winningInfo.successAt() : null;

		return AuctionDetailResponse.of(
			auction, TimeFormatter.formatRemainingTime(now, auction), winningNickname, null, null, successAt
		);
	}

	// 경매를 삭제합니다.
	@Transactional
	public void deleteAuction(AuthUser authUser, Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		auctionServiceHelper.validateOwnership(authUser, auction);
		auctionServiceHelper.validateDeletableAuction(auction);

		auction.markAsDeleted();
	}

	// 상태 기준으로 경매를 조회합니다.
	private Auction findAuctionByIdAndStatus(Long auctionId, List<AuctionStatus> statuses) {
		return auctionRepository.findByIdAndStatusIn(auctionId, statuses)
			.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));
	}

	// 경매 시작/종료 알림을 예약합니다.
	private void reserveMyAuctionNotification(Auction auction) {
		User seller = auction.getItem().getSeller();

		RedisNotification startNotification = redisNotificationFactory.myAuctionStart(seller, auction);
		notificationService.saveNotification(startNotification);

		RedisNotification endNotification = redisNotificationFactory.myAuctionEnd(seller, auction);
		notificationService.saveNotification(endNotification);
	}
}

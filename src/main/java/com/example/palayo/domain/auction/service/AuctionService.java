package com.example.palayo.domain.auction.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.example.palayo.domain.auction.job.AuctionEndJob;
import com.example.palayo.domain.auction.job.AuctionStartJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

@Slf4j
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
	private final Scheduler scheduler;

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

		// 저장된 경매 객체에서 auctionId 확인
		Long auctionId = savedAuction.getId();

		// 만약 auctionId가 null이면 예외를 던지거나 경고 로그 출력
		if (auctionId == null) {
			System.out.println("[ERROR] Auction ID is null after saving the auction!");
			throw new BaseException(ErrorCode.QUARTZ_SCHEDULER_ERROR, "경매 ID가 null입니다.");
		}
		// 예약된 경매 시작 작업을 위해 스케줄링
		scheduleAuctionStartJob(auction); // 경매 시작 작업 예약
		// 경매 종료 작업 예약 (Quartz 예약)
		scheduleAuctionEndJob(savedAuction);

		// 알림 예약 (경매 시작/종료 알림)
		reserveMyAuctionNotification(savedAuction);

		return AuctionResponse.of(savedAuction);
	}
	private void scheduleAuctionStartJob(Auction auction) {
		try {
			Long auctionId = auction.getId();
			if (auctionId == null) {
				log.warn("[ERROR] auctionId is null before scheduling start job.");
			}
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("auctionId", auctionId);

			JobDetail jobDetail = JobBuilder.newJob(AuctionStartJob.class)
					.withIdentity("auctionStartJob_" + auctionId)
					.usingJobData(jobDataMap)
					.build();

			Trigger startTrigger = TriggerBuilder.newTrigger()
					.withIdentity("auctionStartTrigger_" + auctionId)
					.startAt(Date.from(auction.getStartedAt().atZone(ZoneId.systemDefault()).toInstant()))
					.build();

			log.warn("[Quartz] AuctionStartJob 예약됨: auctionId = " + auctionId);

			scheduler.scheduleJob(jobDetail, startTrigger);
		} catch (SchedulerException e) {
			throw new BaseException(ErrorCode.QUARTZ_SCHEDULER_ERROR, "경매 시작 작업 예약에 실패했습니다.");
		}
	}
	//    경매 상태를 갱신합니다.
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
	//쿼츠관련
	@Transactional
	public void markAuctionAsActive(Long auctionId) {
		// auctionId가 null인 경우 예외를 던짐
		if (auctionId == null) {
			throw new BaseException(ErrorCode.USER_NOT_FOUND, "옥션 id가 널이야!");
		}

		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		auction.markAsActive(); // 상태를 ACTIVE로 바꿔줌
	}

	@Transactional
	public void finishAuction(Long auctionId) {
		log.warn("✅ [finishAuction] 시작: auctionId = {}", auctionId);

		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		if (auction.getStatus() == AuctionStatus.SUCCESS || auction.getStatus() == AuctionStatus.FAILED) {
			log.warn("⚠️ [finishAuction] 이미 종료된 경매: auctionId = {}", auctionId);
			return;
		}

		if (auction.getExpiredAt().isBefore(LocalDateTime.now())) {
			// 최고 입찰자 조회
			auctionHistoryRepository
					.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(auction.getId())
					.ifPresentOrElse(topBid -> {
						auction.markAsSuccess(topBid.getBidder(), LocalDateTime.now());

						// 최고 입찰자 알림 전송
						RedisNotification notification = redisNotificationFactory.bidEnd(topBid.getBidder(), auction);
						notificationService.saveNotification(notification);

						log.warn("✅ [finishAuction] SUCCESS 처리됨: auctionId = {}, 낙찰자 = {}", auctionId, topBid.getBidder().getId());
					}, () -> {
						auction.markAsFailed();
						log.warn("✅ [finishAuction] FAILED 처리됨 (입찰자 없음): auctionId = {}", auctionId);
					});
		} else {
			log.warn("[finishAuction] 아직 만료되지 않음: auctionId = {}", auctionId);
			return;
		}

		auctionRepository.save(auction);
		log.warn("[finishAuction] 저장 완료: auctionId = {}", auctionId);
	}
	//쿼츠관련
	private void scheduleAuctionEndJob(Auction auction) {
		try {
			// 경매 종료 시간을 Quartz의 Trigger에 설정
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("auctionId", auction.getId());

			// AuctionEndJob 정의
			JobDetail jobDetail = JobBuilder.newJob(AuctionEndJob.class)
					.withIdentity("auctionEndJob_" + auction.getId())
					.usingJobData(jobDataMap)
					.build();

			// 종료 시간에 맞춰 Trigger 설정
			Trigger endTrigger = TriggerBuilder.newTrigger()
					.withIdentity("auctionEndTrigger_" + auction.getId())
					.startAt(Date.from(auction.getExpiredAt().atZone(ZoneId.systemDefault()).toInstant())) // 경매 종료 시간에 맞춰 설정
					.build();

//          테스트용 로그 (필요 시 주석 해제)
			System.out.println("[Quartz] AuctionEndJob 예약됨: auctionId = " + auction.getId());

			// Job 예약
			scheduler.scheduleJob(jobDetail, endTrigger);
		} catch (SchedulerException e) {
			// 예외 처리
			throw new BaseException(ErrorCode.QUARTZ_SCHEDULER_ERROR, "경매 종료 작업 예약에 실패했습니다.");
		}
	}
}

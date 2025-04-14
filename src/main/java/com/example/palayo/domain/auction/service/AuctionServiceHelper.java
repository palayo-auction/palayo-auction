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
import com.example.palayo.domain.auctionhistory.service.AuctionHistoryServiceHelper;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionServiceHelper {

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final AuctionHistoryServiceHelper auctionHistoryServiceHelper;
	private final RedisNotificationFactory redisNotificationFactory;
	private final NotificationService notificationService;

	// 경매의 현재 시간에 따라 상태를 변경하는 메서드
	// (READY, ACTIVE, SUCCESS, FAILED 등으로 변경)
	public boolean updateStatus(Auction auction) {
		LocalDateTime now = LocalDateTime.now();

		// 즉시구매가 도달했으면 바로 성공처리
		if (isBuyoutPriceReached(auction)) {
			updateToSuccess(auction);
			return true;
		}

		// 아직 시작 전이라면 READY 상태로 설정
		if (AuctionTimeUtils.isBeforeStart(now, auction)) {
			auction.markAsReady();
			return true;
		}

		// 진행 중이면 ACTIVE 상태로 설정
		if (AuctionTimeUtils.isDuringAuction(now, auction)) {
			auction.markAsActive();
			return true;
		}

		// 경매 시간이 끝났으면 성공 또는 실패 처리
		if (AuctionTimeUtils.isAfterEnd(now, auction)) {
			updateAfterExpired(auction);
			return true;
		}

		// 아무 상태도 변하지 않았다면 false 반환
		return false;
	}

	// 경매 종료 시 낙찰자를 선정하는 메서드
	public boolean assignWinningBidder(Auction auction) {
		// 입찰 기록이 없다면 낙찰자 지정 불가
		if (!hasBids(auction)) {
			sendBidFailNotifications(auction);
			return false;
		}

		// 아직 낙찰자가 지정되지 않았다면 최고 입찰자를 낙찰자로 설정
		if (auction.getWinningBidder() == null) {
			AuctionHistory topBid = auctionHistoryRepository.findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(
					auction.getId())
				.orElseThrow(() -> new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId"));

			auction.setWinningBidder(topBid.getBidder());
			sendBidSuccessNotification(auction);
		}

		// 즉시구매가 도달했으면 성공 처리
		if (isBuyoutPriceReached(auction)) {
			updateToSuccess(auction);
			return true;
		}

		// 경매 시간이 종료되었으면 성공/실패 처리
		if (AuctionTimeUtils.isAfterEnd(LocalDateTime.now(), auction)) {
			updateAfterExpired(auction);
			return true;
		}

		// 입찰 실패자에게 유찰 알림 전송
		sendBidFailNotifications(auction);
		return false;
	}

	// 경매의 소유자(판매자)와 요청자가 일치하는지 검증하는 메서드
	public void validateOwnership(AuthUser authUser, Auction auction) {
		if (!auction.getItem().getSeller().getId().equals(authUser.getUserId())) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS, "auctionId");
		}
	}

	// 경매가 삭제 가능한 상태인지 검증하는 메서드
	public void validateDeletableAuction(Auction auction) {
		// 이미 삭제된 경매는 다시 삭제할 수 없음
		if (auction.getStatus() == AuctionStatus.DELETED) {
			throw new BaseException(ErrorCode.ALREADY_DELETED_AUCTION, "auctionId");
		}

		// READY, SUCCESS, FAILED 상태만 삭제 가능 (ACTIVE는 삭제 불가)
		if (!(auction.getStatus() == AuctionStatus.READY
			|| auction.getStatus() == AuctionStatus.SUCCESS
			|| auction.getStatus() == AuctionStatus.FAILED)) {
			throw new BaseException(ErrorCode.CANNOT_DELETE_ACTIVE_AUCTION, "auctionId");
		}
	}

	// 경매에 입찰 기록이 있는지 확인하는 메서드
	private boolean hasBids(Auction auction) {
		return auctionHistoryRepository.existsByAuctionId(auction.getId());
	}

	// 현재 경매가 즉시구매가에 도달했는지 확인하는 메서드
	private boolean isBuyoutPriceReached(Auction auction) {
		if (!hasBids(auction)) {
			return false;
		}
		return auction.getCurrentPrice() >= auction.getBuyoutPrice();
	}

	// 낙찰자가 있을 때 경매를 SUCCESS 상태로 변경하고 후처리하는 메서드
	private void updateToSuccess(Auction auction) {
		if (auction.getWinningBidder() == null) {
			throw new BaseException(ErrorCode.NO_WINNING_BIDDER, "auctionId");
		}

		// 경매 상태를 SUCCESS로 변경
		auction.markAsSuccess(auction.getWinningBidder());

		// 낙찰자 포인트 차감 및 보증금 사용 처리
		auctionHistoryServiceHelper.handleAuctionSuccess(
			auction,
			auction.getWinningBidder(),
			auction.getCurrentPrice()
		);

		// 실패자 보증금 환불 처리
		auctionHistoryServiceHelper.refundFailedBidders(auction);
	}

	// 경매 종료 후 낙찰 성공/실패를 최종 처리하는 메서드
	private void updateAfterExpired(Auction auction) {
		if (auction.getWinningBidder() != null) {
			// 낙찰자가 있으면 SUCCESS 처리
			auction.markAsSuccess(auction.getWinningBidder());

			auctionHistoryServiceHelper.handleAuctionSuccess(
				auction,
				auction.getWinningBidder(),
				auction.getCurrentPrice()
			);

			auctionHistoryServiceHelper.refundFailedBidders(auction);
		} else {
			// 낙찰자가 없으면 FAILED 처리
			auction.markAsFailed();
		}
	}

	// 낙찰 성공 알림 전송 메서드
	private void sendBidSuccessNotification(Auction auction) {
		RedisNotification winNotice = redisNotificationFactory.bidWin(auction.getWinningBidder(), auction);
		notificationService.saveNotification(winNotice);
	}

	// 입찰 실패자에게 유찰 알림 전송 메서드
	private void sendBidFailNotifications(Auction auction) {
		List<User> participants = auctionHistoryRepository.findAllBiddersByAuctionId(auction.getId());
		for (User user : participants) {
			RedisNotification failNotice = redisNotificationFactory.bidFail(user, auction);
			notificationService.saveNotification(failNotice);
		}
	}
}

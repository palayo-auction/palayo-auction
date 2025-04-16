package com.example.palayo.domain.auction.util;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;

// 경매 시간 기준으로 상태를 판별하는 유틸리티 클래스
public class AuctionTimeUtils {

	// 인스턴스 생성을 막기 위한 private 생성자
	private AuctionTimeUtils() {
	}

	// 현재 시간이 경매 시작 시간 이전인지 확인
	public static boolean isBeforeStart(LocalDateTime now, Auction auction) {
		return now.isBefore(auction.getStartedAt());
	}

	// 현재 시간이 경매 진행 구간에 포함되는지 확인
	public static boolean isDuringAuction(LocalDateTime now, Auction auction) {
		return now.isAfter(auction.getStartedAt()) && now.isBefore(auction.getExpiredAt());
	}

	// 현재 시간이 경매 종료 시간을 지났는지 확인
	public static boolean isAfterEnd(LocalDateTime now, Auction auction) {
		return now.isAfter(auction.getExpiredAt());
	}

	// 경매 종료 5분 전 알림을 보내야 하는 시간대인지 확인
	public static boolean isAboutToExpireInFiveMinutes(Auction auction) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expireAt = auction.getExpiredAt();

		return now.isAfter(expireAt.minusMinutes(5).minusSeconds(30)) &&
			now.isBefore(expireAt.minusMinutes(5).plusSeconds(30));
	}
}

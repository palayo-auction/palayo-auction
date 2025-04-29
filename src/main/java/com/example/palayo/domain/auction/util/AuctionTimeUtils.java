package com.example.palayo.domain.auction.util;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;

// 경매 시간 관련 유틸리티 클래스
public class AuctionTimeUtils {

	// 생성자 private 처리: 인스턴스 생성 방지
	private AuctionTimeUtils() {
	}

	// 현재 시간이 경매 시작 전인지 여부
	public static boolean isBeforeStart(LocalDateTime now, Auction auction) {
		return now.isBefore(auction.getStartedAt());
	}

	// 현재 시간이 경매 진행 시간대인지 여부
	public static boolean isDuringAuction(LocalDateTime now, Auction auction) {
		return now.isAfter(auction.getStartedAt()) && now.isBefore(auction.getExpiredAt());
	}

	// 현재 시간이 경매 종료 이후인지 여부
	public static boolean isAfterEnd(LocalDateTime now, Auction auction) {
		return now.isAfter(auction.getExpiredAt());
	}

	// 현재가 종료 5분 전 ±30초 사이인지 여부 (알림 타이밍 체크용)
	public static boolean isAboutToExpireInFiveMinutes(Auction auction) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expireAt = auction.getExpiredAt();

		return now.isAfter(expireAt.minusMinutes(5).minusSeconds(30)) &&
			now.isBefore(expireAt.minusMinutes(5).plusSeconds(30));
	}
}

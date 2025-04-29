package com.example.palayo.domain.auction.util;

import java.time.Duration;
import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;

// 경매 남은 시간을 포맷팅하는 유틸리티 클래스
public class TimeFormatter {

	// 인스턴스 생성을 방지합니다.
	private TimeFormatter() {
	}

	// 현재 시간 기준으로 경매 남은 시간을 "X일 X시간 X분 X초" 포맷으로 반환합니다.
	public static String formatRemainingTime(LocalDateTime now, Auction auction) {
		if (isAuctionEnded(auction)) {
			return "경매 종료";
		}

		// 남은 시간 계산 (음수 방지)
		long totalSeconds = Math.max(Duration.between(now, auction.getExpiredAt()).getSeconds(), 0);

		long days = totalSeconds / (24 * 3600);
		long hours = (totalSeconds % (24 * 3600)) / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;

		return String.format("%d일 %d시간 %d분 %d초", days, hours, minutes, seconds);
	}

	// 경매가 종료 상태인지 확인합니다.
	private static boolean isAuctionEnded(Auction auction) {
		return auction.getStatus() == AuctionStatus.SUCCESS
			|| auction.getStatus() == AuctionStatus.FAILED
			|| auction.getStatus() == AuctionStatus.DELETED;
	}
}
package com.example.palayo.domain.auction.util;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;

// 경매 남은 시간을 문자열로 포맷팅하는 유틸리티 클래스
public class TimeFormatter {

	// 인스턴스 생성을 막기 위한 private 생성자
	private TimeFormatter() {
	}

	// 현재 시각 기준으로 남은 시간을 "X일 X시간 X분 X초" 포맷으로 변환
	public static String formatRemainingTime(LocalDateTime now, Auction auction) {

		// 경매가 종료된 상태(SUCCESS, FAILED, DELETED)면 "경매 종료" 반환
		if (isAuctionEnded(auction)) {
			return "경매 종료";
		}

		// 경매 진행 중이면 남은 시간 계산 (음수 방지를 위해 0 이상만 허용)
		long totalSeconds = Math.max(
			java.time.Duration.between(now, auction.getExpiredAt()).getSeconds(),
			0
		);

		long days = totalSeconds / (24 * 3600);
		long hours = (totalSeconds % (24 * 3600)) / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;

		return String.format("%d일 %d시간 %d분 %d초", days, hours, minutes, seconds);
	}

	// 경매 상태가 SUCCESS, FAILED, DELETED 중 하나인지 확인
	private static boolean isAuctionEnded(Auction auction) {
		return auction.getStatus() == AuctionStatus.SUCCESS
			|| auction.getStatus() == AuctionStatus.FAILED
			|| auction.getStatus() == AuctionStatus.DELETED;
	}
}
package com.example.palayo.domain.auction.enums;

public enum AuctionStatus {
	READY, // 경매 대기 중 (등록 완료, 경매 시작 전)
	ACTIVE, // 경매 진행 중 (입찰 가능)
	SUCCESS, // 경매 성공 (낙찰자 확정)
	FAILED, // 경매 유찰 (낙찰자 없이 종료)
	DELETED // 경매 삭제됨 (소프트 딜리트 처리)
}

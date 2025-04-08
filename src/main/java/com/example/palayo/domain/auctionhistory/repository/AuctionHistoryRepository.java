package com.example.palayo.domain.auctionhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;

public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {

	// 경매 ID로 입찰 기록이 존재하는지 확인
	boolean existsByAuctionId(Long auctionId);
}
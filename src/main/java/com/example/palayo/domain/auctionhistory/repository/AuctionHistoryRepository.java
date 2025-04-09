package com.example.palayo.domain.auctionhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {

	// 경매 ID로 입찰 기록이 존재하는지 확인
	boolean existsByAuctionId(Long auctionId);

	//경매에 참가한 사용자에게 알림을 보내기 위해 중복제거 후 입찰자id 확인
	@Query("SELECT DISTINCT ah.bidder.id FROM AuctionHistory ah WHERE ah.auction.id = :auctionId")
	List<Long> findDistinctBidderIdsByAuctionId(Long auctionId);
}
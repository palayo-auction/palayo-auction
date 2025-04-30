package com.example.palayo.domain.auctionhistory.repository;

import com.example.palayo.domain.auctionhistory.entity.AuctionHistory;
import com.example.palayo.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {

	// 경매 ID + 입찰자 ID로 입찰 기록 존재 여부 조회
	boolean existsByAuctionIdAndBidderId(Long auctionId, Long bidderId);

	// 경매 ID로 입찰 기록 존재 여부 조회
	boolean existsByAuctionId(Long auctionId);

	// 경매 ID로 가장 높은 입찰 기록 조회 (가격 높은 순, 입찰 시간 빠른 순)
	Optional<AuctionHistory> findTopByAuctionIdOrderByBidPriceDescCreatedAtAsc(Long auctionId);

	// 경매 ID로 입찰 기록 조회
	List<AuctionHistory> findByAuctionId(Long auctionId);

	// 경매 ID로 입찰 기록 페이징 조회
	Page<AuctionHistory> findByAuctionId(Long auctionId, Pageable pageable);

	// 사용자 ID로 입찰한 경매 ID 목록 조회 (중복 제거)
	@Query("SELECT DISTINCT ah.auction.id FROM AuctionHistory ah WHERE ah.bidder.id = :userId")
	List<Long> findDistinctAuctionIdsByBidderId(@Param("userId") Long userId);

	// 경매 ID로 입찰자 목록 조회 (중복 제거)
	@Query("SELECT DISTINCT ah.bidder FROM AuctionHistory ah WHERE ah.auction.id = :auctionId")
	List<User> findAllBiddersByAuctionId(@Param("auctionId") Long auctionId);

	// 경매ID + 입찰자 ID로 가장 높은 입찰 (시간 최신 순)
	Optional<AuctionHistory> findTopByAuctionIdAndBidderIdOrderByBidPriceDescCreatedAtDesc(Long auctionId,
		Long bidderId);

	// ACTIVE 상태 경매 중 각 경매에서 사용자의 최고 입찰가만 합산
	@Query("SELECT COALESCE(SUM(ah.bidPrice), 0) " +
		"FROM AuctionHistory ah " +
		"WHERE ah.bidder.id = :userId " +
		"AND ah.auction.status = 'ACTIVE' " +
		"AND ah.auction.winningBidder IS NULL " +
		"AND ah.bidPrice = (" +
		"  SELECT MAX(sub.bidPrice) FROM AuctionHistory sub " +
		"  WHERE sub.auction.id = ah.auction.id " +
		"  AND sub.bidder.id = :userId)")
	Long sumMaxBidPricesByUserIdOnActiveAuctions(@Param("userId") Long userId);
}

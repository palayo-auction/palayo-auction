package com.example.palayo.domain.deposithistory.repository;

import java.util.List;
import java.util.Optional;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositHistoryRepository extends JpaRepository<DepositHistory, Long> {

	// Auction과 User로 보증금 이력이 존재하는지 확인하는 메서드
	boolean existsByAuctionAndUser(Auction auction, User user);

	// Auction과 User로 페이징된 보증금 이력 조회
	Page<DepositHistory> findByAuctionAndUser(Auction auction, User user, Pageable pageable);

	// 경매 ID와 사용자 ID로 보증금 이력 존재 여부 확인
	boolean existsByAuctionIdAndUserId(Long auctionId, Long userId);

	// 경매 ID로 모든 보증금 이력 조회 (보증금 환불 처리용)
	List<DepositHistory> findAllByAuctionId(Long auctionId);

	Optional<DepositHistory> findByAuctionAndUser(Auction auction, User user);
}

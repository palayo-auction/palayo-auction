package com.example.palayo.domain.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

	// 상품 ID로 경매 단건 조회
	Optional<Auction> findOptionalByItemId(Long itemId);

	// 상품 ID로 특정 상태(READY, ACTIVE)인 경매 존재 여부 조회
	boolean existsByItemIdAndStatusIn(Long itemId, List<AuctionStatus> statuses);

	// 경매 ID로 특정 상태(READY, ACTIVE)인 경매 단건 조회
	Optional<Auction> findByIdAndStatusIn(Long id, List<AuctionStatus> statuses);

	// 특정 상태(READY, ACTIVE)인 경매 다건 조회 (페이징)
	Page<Auction> findAllByStatusIn(List<AuctionStatus> statuses, Pageable pageable);

	// 판매자 ID로 특정 상태(READY, ACTIVE, SUCCESS, FAILED)인 경매 다건 조회 (페이징)
	Page<Auction> findAllByItemSellerIdAndStatusIn(Long sellerId, List<AuctionStatus> statuses, Pageable pageable);

	// 주어진 경매 ID 목록 + 특정 상태 조건으로 경매 다건 조회 (페이징)
	Page<Auction> findAllByIdInAndStatusIn(List<Long> auctionIds, List<AuctionStatus> statuses, Pageable pageable);

	// 특정 상태(READY, ACTIVE)인 경매 전체 조회 (스케줄러용)
	List<Auction> findAllByStatusIn(List<AuctionStatus> statuses);

	// 낙찰자 ID로 경매 목록 조회 (페이징) — 사용자가 낙찰받은 경매 내역 조회 시 사용
	Page<Auction> findByWinningBidder_Id(Long winningBidderId, Pageable pageable);
}

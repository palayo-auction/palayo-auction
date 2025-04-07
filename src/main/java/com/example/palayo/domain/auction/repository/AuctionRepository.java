package com.example.palayo.domain.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

	// 상품 ID로 READY 또는 ACTIVE 상태인 경매가 존재하는지 확인
	boolean existsByItemIdAndStatusIn(Long itemId, List<AuctionStatus> statuses);

	// 경매 ID로 READY 또는 ACTIVE 상태인 경매 단건 조회
	Optional<Auction> findByAuctionIdAndStatusIn(Long id, List<AuctionStatus> statuses);

	// READY 또는 ACTIVE 상태인 경매를 페이징 조회
	Page<Auction> findAllByStatusIn(List<AuctionStatus> statuses, Pageable pageable);

	// READY 또는 ACTIVE 상태인 경매 전체 조회 (스케줄러에서 상태 업데이트용)
	List<Auction> findAllByStatusIn(List<AuctionStatus> statuses);

	// 판매자 ID로 READY, ACTIVE, SUCCESS, FAILED, DELETED 상태인 경매를 페이징 조회
	Page<Auction> findAllByItemSellerIdAndStatusIn(Long sellerId, List<AuctionStatus> statuses, Pageable pageable);
}

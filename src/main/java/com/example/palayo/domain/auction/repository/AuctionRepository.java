package com.example.palayo.domain.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;

import jakarta.persistence.LockModeType;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

	// 상품 ID로 특정 상태(READY, ACTIVE)인 경매 존재 여부 조회
	boolean existsByItemIdAndStatusIn(Long itemId, List<AuctionStatus> statuses);

	// 경매 ID로 특정 상태(READY, ACTIVE)인 경매 단건 조회
	Optional<Auction> findByIdAndStatusIn(Long id, List<AuctionStatus> statuses);

	// 특정 상태(READY, ACTIVE)인 경매 다건 조회 (페이징)
	Page<Auction> findAllByStatusIn(List<AuctionStatus> statuses, Pageable pageable);

	// 판매자 ID로 특정 상태(READY, ACTIVE, SUCCESS, FAILED, DELETED)인 경매 다건 조회 (페이징)
	Page<Auction> findAllByItemSellerIdAndStatusIn(Long sellerId, List<AuctionStatus> statuses, Pageable pageable);

	// 주어진 경매 ID 목록 + 특정 상태 조건으로 경매 다건 조회 (페이징)
	Page<Auction> findAllByIdInAndStatusIn(List<Long> auctionIds, List<AuctionStatus> statuses, Pageable pageable);

	// 특정 상태(READY, ACTIVE)인 경매 전체 조회 (스케줄러용)
	List<Auction> findAllByStatusIn(List<AuctionStatus> statuses);

	// 경매를 조회할 때 데이터베이스 레벨에서 락을 걸어 다른 트랜잭션의 수정을 막습니다
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Auction a WHERE a.id = :id")
	Optional<Auction> findByIdForUpdate(@Param("id") Long id);

	Optional<Auction> findByItemId(Long itemId);
}

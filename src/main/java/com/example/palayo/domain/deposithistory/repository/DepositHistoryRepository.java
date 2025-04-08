package com.example.palayo.domain.deposithistory.repository;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DepositHistoryRepository extends JpaRepository<DepositHistory, Long> {

    // Auction ID로 보증금 이력 조회
    List<DepositHistory> findByAuctionId(Long auctionId);

    // User ID로 보증금 이력 조회
    List<DepositHistory> findByUserId(Long userId);

    // 상태로 보증금 이력 조회
    List<DepositHistory> findByStatus(String status);

    // 생성일 사이의 보증금 이력 조회
    List<DepositHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Auction ID로 페이징된 보증금 이력 조회
    Page<DepositHistory> findByAuctionId(Long auctionId, Pageable pageable);

    // User ID로 페이징된 보증금 이력 조회
    Page<DepositHistory> findByUserId(Long userId, Pageable pageable);

    // 상태로 페이징된 보증금 이력 조회
    Page<DepositHistory> findByStatus(String status, Pageable pageable);

    // 생성일 사이의 페이징된 보증금 이력 조회
    Page<DepositHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Auction과 User로 보증금 이력이 존재하는지 확인하는 메서드
    boolean existsByAuctionAndUser(Auction auction, User user);
}

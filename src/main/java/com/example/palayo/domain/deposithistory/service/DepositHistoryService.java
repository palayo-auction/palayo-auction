package com.example.palayo.domain.deposithistory.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryRequest;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepositHistoryService {

    private final EntityManager entityManager;
    private final DepositHistoryRepository depositHistoryRepository;

    // 단건 조회 (DTO 변환)
    @Transactional(readOnly = true)
    public DepositHistoryResponse getDepositHistory(Long id) {
        DepositHistory depositHistory = depositHistoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "id"));
        return DepositHistoryResponse.fromEntity(depositHistory);
    }

    // 다건 조회 (페이징 처리 포함)
    @Transactional(readOnly = true)
    public Page<DepositHistoryResponse> getDepositHistoryList(Long auctionId, Long userId, String status, Pageable pageable) {
        // Auction 조회 (EntityManager 사용)
        String auctionQuery = "SELECT a FROM Auction a WHERE a.id = :auctionId";
        TypedQuery<Auction> auctionTypedQuery = entityManager.createQuery(auctionQuery, Auction.class);
        auctionTypedQuery.setParameter("auctionId", auctionId);
        Auction auction = auctionTypedQuery.getSingleResult();

        if (auction == null) {
            throw new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId");
        }

        // User 조회 (EntityManager 사용)
        String userQuery = "SELECT u FROM User u WHERE u.id = :userId";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(userQuery, User.class);
        userTypedQuery.setParameter("userId", userId);
        User user = userTypedQuery.getSingleResult();

        if (user == null) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND, "userId");
        }

        // DepositHistory 조회 (페이징 처리)
        String depositQuery = "SELECT d FROM DepositHistory d WHERE d.auction = :auction AND d.user = :user";
        TypedQuery<DepositHistory> depositTypedQuery = entityManager.createQuery(depositQuery, DepositHistory.class);
        depositTypedQuery.setParameter("auction", auction);
        depositTypedQuery.setParameter("user", user);

        // 페이징 처리 (setFirstResult: 시작 위치, setMaxResults: 최대 결과 수)
        depositTypedQuery.setFirstResult((int) pageable.getOffset());
        depositTypedQuery.setMaxResults(pageable.getPageSize());

        List<DepositHistory> depositHistories = depositTypedQuery.getResultList();

        // 전체 결과 수 조회 (페이징을 위해 필요)
        String countQuery = "SELECT COUNT(d) FROM DepositHistory d WHERE d.auction = :auction AND d.user = :user";
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery, Long.class);
        countTypedQuery.setParameter("auction", auction);
        countTypedQuery.setParameter("user", user);
        Long totalCount = countTypedQuery.getSingleResult();

        // 결과를 DTO로 변환하여 Page 객체로 반환
        List<DepositHistoryResponse> responseDTOs = depositHistories.stream()
                .map(DepositHistoryResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDTOs, pageable, totalCount);
    }

    // 보증금 이력 생성
    @Transactional
    public DepositHistoryResponse createDepositHistory(DepositHistoryRequest depositHistoryRequestDTO) {
        // Auction 조회 (EntityManager 사용)
        String auctionQuery = "SELECT a FROM Auction a WHERE a.id = :auctionId";
        TypedQuery<Auction> auctionTypedQuery = entityManager.createQuery(auctionQuery, Auction.class);
        auctionTypedQuery.setParameter("auctionId", depositHistoryRequestDTO.getAuctionId());
        Auction auction = auctionTypedQuery.getSingleResult();

        if (auction == null) {
            throw new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId");
        }

        // User 조회 (EntityManager 사용)
        String userQuery = "SELECT u FROM User u WHERE u.id = :userId";
        TypedQuery<User> userTypedQuery = entityManager.createQuery(userQuery, User.class);
        userTypedQuery.setParameter("userId", depositHistoryRequestDTO.getUserId());
        User user = userTypedQuery.getSingleResult();

        if (user == null) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND, "userId");
        }

        // DepositHistory 생성
        DepositHistory depositHistory = new DepositHistory(auction, user, depositHistoryRequestDTO.getDeposit(), depositHistoryRequestDTO.getStatus());

        // DepositHistory가 이미 존재하는지 확인 (예: 사용자와 경매 조합이 동일한 이력이 있을 경우)
        if (depositHistoryRepository.existsByAuctionAndUser(auction, user)) {
            throw new BaseException(ErrorCode.DEPOSIT_HISTORY_ALREADY_EXISTS, "auctionId, userId");
        }

        DepositHistory savedDepositHistory = depositHistoryRepository.save(depositHistory);

        return DepositHistoryResponse.fromEntity(savedDepositHistory);
    }

    // 보증금 이력 수정
    @Transactional
    public DepositHistoryResponse updateDepositHistory(Long id, DepositHistoryRequest depositHistoryRequest) {
        DepositHistory depositHistory = depositHistoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "id"));

        // 수정된 값 적용
        depositHistory.setDeposit(depositHistoryRequest.getDeposit());
        depositHistory.setStatus(depositHistoryRequest.getStatus());  // DepositStatus로 바로 사용

        // 업데이트된 데이터 저장
        DepositHistory updatedDepositHistory = depositHistoryRepository.save(depositHistory);
        return DepositHistoryResponse.fromEntity(updatedDepositHistory);
    }

    // 보증금 이력 삭제
    @Transactional
    public void deleteDepositHistory(Long id) {
        DepositHistory depositHistory = depositHistoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "id"));
        depositHistoryRepository.delete(depositHistory);
    }
}
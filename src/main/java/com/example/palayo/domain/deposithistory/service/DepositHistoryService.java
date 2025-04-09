package com.example.palayo.domain.deposithistory.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryRequest;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepositHistoryService {

    private final DepositHistoryRepository depositHistoryRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    // 단건 조회 (DTO 변환)
    @Transactional(readOnly = true)
    public DepositHistoryResponse getDepositHistory(Long id, AuthUser authUser) {
        DepositHistory depositHistory = depositHistoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "id"));
        return DepositHistoryResponse.fromEntity(depositHistory);
    }

    // 다건 조회 (페이징 처리 포함)
    @Transactional(readOnly = true)
    public Page<DepositHistoryResponse> getDepositHistoryList(Long auctionId, String status, int page, int size, AuthUser authUser) {
        // Auction 조회 (레포지토리 사용)
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

        // AuthUser에서 userId를 추출하여 User 조회
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getUserId().toString()));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // DepositHistory 조회 (페이징 처리)
        Page<DepositHistory> depositHistoryPage = depositHistoryRepository.findByAuctionAndUser(auction, user, pageable);

        // DepositHistory를 DepositHistoryResponse로 변환 후 반환
        return depositHistoryPage.map(DepositHistoryResponse::fromEntity);
    }

    // 보증금 이력 생성
    @Transactional
    public DepositHistoryResponse createDepositHistory(DepositHistoryRequest depositHistoryRequestDTO, AuthUser authUser) {
        // Auction 조회 (레포지토리 사용)
        Auction auction = auctionRepository.findById(depositHistoryRequestDTO.getAuctionId())
                .orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

        // AuthUser에서 userId를 추출하여 User 조회
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getUserId().toString()));

        // DepositHistory 생성
        DepositHistory depositHistory = new DepositHistory(auction, user, depositHistoryRequestDTO.getDeposit(), depositHistoryRequestDTO.getStatus());

        // DepositHistory가 이미 존재하는지 확인 (예: 사용자와 경매 조합이 동일한 이력이 있을 경우)
        if (depositHistoryRepository.existsByAuctionAndUser(auction, user)) {
            throw new BaseException(ErrorCode.DEPOSIT_HISTORY_ALREADY_EXISTS, "The deposit history for this auction already exists.");
        }

        DepositHistory savedDepositHistory = depositHistoryRepository.save(depositHistory);

        return DepositHistoryResponse.fromEntity(savedDepositHistory);
    }
}


package com.example.palayo.domain.deposithistory.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
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
	public DepositHistoryResponse getDepositHistory(Long id) {
		DepositHistory depositHistory = depositHistoryRepository.findById(id)
				.orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "id"));
		return DepositHistoryResponse.fromEntity(depositHistory);
	}

	// 다건 조회 (페이징 처리 포함)
	@Transactional(readOnly = true)
	public Page<DepositHistoryResponse> getDepositHistoryList(Long auctionId, int page, int size,
															  AuthUser authUser) {
		// Auction 조회 (레포지토리 사용)
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// AuthUser에서 userId를 추출하여 User 조회
		User user = userRepository.findById(authUser.getUserId())
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getUserId().toString()));

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// DepositHistory 조회 (페이징 처리)
		Page<DepositHistory> depositHistoryPage = depositHistoryRepository.findByAuctionAndUser(auction, user,
				pageable);

		// DepositHistory를 DepositHistoryResponse로 변환 후 반환
		return depositHistoryPage.map(DepositHistoryResponse::fromEntity);
	}

	// 사용자가 해당 경매에 보증금을 이미 납부했는지 확인
	@Transactional(readOnly = true)
	public boolean existsByAuctionAndUser(Long auctionId, Long userId) {
		return depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId);
	}

	// 유저 ID, 경매 ID, 보증금 금액을 받아 보증금 이력을 생성
	@Transactional
	public void createDepositHistory(Long userId, Long auctionId, int depositAmount) {

		// Auction 조회
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		// User 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

		// 이미 보증금 이력이 있는지 확인
		boolean alreadyExists = depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId);
		if (alreadyExists) {
			throw new BaseException(ErrorCode.DEPOSIT_HISTORY_ALREADY_EXISTS, "auctionId, userId");
		}

		// 보증금 이력 생성
		DepositHistory depositHistory = new DepositHistory(
				auction,
				user,
				depositAmount,
				DepositStatus.PENDING
		);

		// 보증금 이력 저장
		depositHistoryRepository.save(depositHistory);
	}

	// 보증금 사용 처리 (낙찰자: PENDING → USED)
	@Transactional
	public void useDeposit(Long auctionId, Long userId) {
		DepositHistory depositHistory = findDepositHistory(auctionId, userId);

		if (depositHistory.getStatus() != DepositStatus.PENDING) {
			throw new BaseException(ErrorCode.INVALID_DEPOSIT_STATUS, "auctionId, userId");
		}

		depositHistory.updateStatus(DepositStatus.USED);
	}

	// 보증금 환불 처리 (실패자: PENDING → REFUNDED)
	@Transactional
	public void refundDeposit(Long auctionId, Long userId) {
		DepositHistory depositHistory = findDepositHistory(auctionId, userId);

		if (depositHistory.getStatus() != DepositStatus.PENDING) {
			throw new BaseException(ErrorCode.INVALID_DEPOSIT_STATUS, "auctionId, userId");
		}

		depositHistory.updateStatus(DepositStatus.REFUNDED);
	}

	// 공통 단건 조회 메서드
	private DepositHistory findDepositHistory(Long auctionId, Long userId) {
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

		return depositHistoryRepository.findByAuctionAndUser(auction, user)
				.orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "auctionId, userId"));
	}
}
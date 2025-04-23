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

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DepositHistoryService {

	private final DepositHistoryRepository depositHistoryRepository;
	private final AuctionRepository auctionRepository;
	private final UserRepository userRepository;
	private final RedissonClient redissonClient;

	// 공통 락 획득 및 작업 수행 메서드
	private void getLockAndPerform(Long auctionId, Long userId, LockAction action) {
		RLock lock = redissonClient.getLock("depositHistoryLock:" + auctionId + ":" + userId);

		try {
			if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
				throw new BaseException(ErrorCode.LOCK_ACQUISITION_FAILED, "락을 획득할 수 없습니다. 잠시 후 다시 시도해 주세요.");
			}
			action.execute();  // 락을 획득한 후 수행할 작업
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // 인터럽트 상태 복구
			throw new BaseException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED, "락 대기 중 인터럽트 발생");
		} finally {
			// 락을 보유한 스레드만 unlock 호출
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

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

		// 이미 처리된 경우: 예외 대신 무시하고 return
		if (depositHistory.getStatus() == DepositStatus.USED) {
			return; // 중복 처리 방지
		}

		if (depositHistory.getStatus() != DepositStatus.PENDING) {
			throw new BaseException(ErrorCode.INVALID_DEPOSIT_STATUS, "auctionId, userId");
		}

		depositHistory.updateStatus(DepositStatus.USED);
	}

	// 보증금 환불 처리 (실패자: PENDING → REFUNDED)
	@Transactional
	public void refundDeposit(Long auctionId, Long userId) {
		DepositHistory depositHistory = findDepositHistory(auctionId, userId);

		// 이미 환불된 경우: 중복 처리 방지
		if (depositHistory.getStatus() == DepositStatus.REFUNDED) {
			return; // 아무 것도 안 하고 종료
		}

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

	// LockAction 인터페이스
	@FunctionalInterface
	public interface LockAction {
		void execute();
	}
}
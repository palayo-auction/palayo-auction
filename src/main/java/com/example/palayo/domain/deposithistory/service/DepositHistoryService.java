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

	// 🔐 공통 락 처리 메서드
	private void getLockAndPerform(Long auctionId, Long userId, LockAction action) {
		RLock lock = redissonClient.getLock("depositHistoryLock:" + auctionId + ":" + userId);

		try {
			if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
				throw new BaseException(ErrorCode.LOCK_ACQUISITION_FAILED, "락을 획득할 수 없습니다. 잠시 후 다시 시도해 주세요.");
			}
			action.execute();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BaseException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED, "락 대기 중 인터럽트 발생");
		} finally {
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

	@Transactional(readOnly = true)
	public Page<DepositHistoryResponse> getDepositHistoryList(Long auctionId, int page, int size, AuthUser authUser) {
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		User user = userRepository.findById(authUser.getUserId())
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getUserId().toString()));

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<DepositHistory> depositHistoryPage = depositHistoryRepository.findByAuctionAndUser(auction, user, pageable);

		return depositHistoryPage.map(DepositHistoryResponse::fromEntity);
	}

	@Transactional(readOnly = true)
	public boolean existsByAuctionAndUser(Long auctionId, Long userId) {
		return depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId);
	}

	@Transactional
	public void createDepositHistory(Long userId, Long auctionId, int depositAmount) {
		getLockAndPerform(auctionId, userId, () -> {
			Auction auction = auctionRepository.findById(auctionId)
					.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

			boolean alreadyExists = depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId);
			if (alreadyExists) {
				throw new BaseException(ErrorCode.DEPOSIT_HISTORY_ALREADY_EXISTS, "auctionId, userId");
			}

			DepositHistory depositHistory = new DepositHistory(
					auction,
					user,
					depositAmount,
					DepositStatus.PENDING
			);

			depositHistoryRepository.save(depositHistory);
		});
	}

	@Transactional
	public void useDeposit(Long auctionId, Long userId) {
		getLockAndPerform(auctionId, userId, () -> {
			DepositHistory depositHistory = findDepositHistory(auctionId, userId);

			if (depositHistory.getStatus() == DepositStatus.USED) {
				return;
			}

			if (depositHistory.getStatus() != DepositStatus.PENDING) {
				throw new BaseException(ErrorCode.INVALID_DEPOSIT_STATUS, "auctionId, userId");
			}

			depositHistory.updateStatus(DepositStatus.USED);
		});
	}

	@Transactional
	public void refundDeposit(Long auctionId, Long userId) {
		getLockAndPerform(auctionId, userId, () -> {
			DepositHistory depositHistory = findDepositHistory(auctionId, userId);

			if (depositHistory.getStatus() == DepositStatus.REFUNDED) {
				return;
			}

			if (depositHistory.getStatus() != DepositStatus.PENDING) {
				throw new BaseException(ErrorCode.INVALID_DEPOSIT_STATUS, "auctionId, userId");
			}

			depositHistory.updateStatus(DepositStatus.REFUNDED);
		});
	}

	private DepositHistory findDepositHistory(Long auctionId, Long userId) {
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, "auctionId"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, "userId"));

		return depositHistoryRepository.findByAuctionAndUser(auction, user)
				.orElseThrow(() -> new BaseException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND, "auctionId, userId"));
	}

	@FunctionalInterface
	public interface LockAction {
		void execute();
	}
}
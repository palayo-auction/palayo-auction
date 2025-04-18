package com.example.palayo.domain.pointhistory.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.pointhistory.dto.PointHistoriesResponse;
import com.example.palayo.domain.pointhistory.entity.PointHistories;
import com.example.palayo.domain.pointhistory.repository.PointHistoriesRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointHistoriesService {

	private final UserRepository userRepository;
	private final PointHistoriesRepository pointHistoriesRepository;

	// 포인트 변경(충전, 차감, 환불) 및 변경 이력 저장
	@Transactional
	public void updatePoints(Long userId, int amount, PointType pointType) {
		User user = findUserById(userId);

		if (pointType == PointType.DECREASE && user.getPointAmount() < amount) {
			throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "포인트 부족");
		}

		user.updatePointAmount(amount);

		PointHistories history = PointHistories.builder()
			.user(user)
			.amount(amount)
			.pointType(pointType)
			.build();

		pointHistoriesRepository.save(history);
	}

	// 사용자 포인트 이력 최신순 조회
	@Transactional(readOnly = true)
	public Page<PointHistoriesResponse> findByUserId(Long userId, int page, int size) {
		findUserById(userId);
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		Page<PointHistories> pointHistories = pointHistoriesRepository.findByUserId(userId, pageable);
		return pointHistories.map(PointHistoriesResponse::of);
	}

	// 사용자 ID로 사용자 조회 (없으면 예외 발생)
	private User findUserById(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, userId.toString()));

		if (user.getDeletedAt() != null) {
			throw new BaseException(ErrorCode.INACTIVE_USER, userId.toString());
		}

		return user;
	}
}

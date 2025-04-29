package com.example.palayo.domain.pointhistory.service;

import java.util.Collections;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.pointhistory.dto.PointHistoriesResponse;
import com.example.palayo.domain.pointhistory.entity.PointHistories;
import com.example.palayo.domain.pointhistory.repository.PointHistoriesRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointHistoriesService {

	private final UserRepository userRepository;
	private final PointHistoriesRepository pointHistoriesRepository;
	private final RedissonClient redissonClient; // Redisson 주입

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

	// 포인트 변경(충전, 차감, 환불) 및 변경 이력 저장 (Lua Script)
	// private static final String DECREASE_LUA_SCRIPT = """
	// 	    local current = redis.call('GET', KEYS[1])
	// 	    if (not current) then
	// 	        return -1
	// 	    end
	// 	    if (tonumber(current) < tonumber(ARGV[1])) then
	// 	        return -2
	// 	    end
	// 	    redis.call('DECRBY', KEYS[1], ARGV[1])
	// 	    return 1
	// 	""";
	//
	// @Transactional
	// public void updatePoints(Long userId, int amount, PointType pointType) {
	//
	// 	// if (amount <= 0) {
	// 	// 	throw new BaseException(ErrorCode.INVALID_POINT_AMOUNT, "포인트 금액은 0보다 커야 합니다.");
	// 	// }
	//
	// 	User user = findUserById(userId);
	//
	// 	String redisKey = "user:point:" + userId;
	//
	// 	if (pointType == PointType.DECREASE) {
	// 		// Lua를 통한 포인트 차감
	// 		Long result = redissonClient.getScript().eval(
	// 			RScript.Mode.READ_WRITE,
	// 			DECREASE_LUA_SCRIPT,
	// 			RScript.ReturnType.INTEGER,
	// 			Collections.singletonList(redisKey),
	// 			amount
	// 		);
	//
	// 		if (result == -1) {
	// 			throw new BaseException(ErrorCode.USER_POINT_NOT_FOUND, "사용자의 포인트 정보가 존재하지 않습니다.");
	// 		} else if (result == -2) {
	// 			throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "포인트가 부족하여 차감할 수 없습니다.");
	// 		}
	// 		// DB 포인트도 차감
	// 		user.updatePointAmount(-amount);
	// 	} else if (pointType == PointType.INCREASE || pointType == PointType.REFUNDED) {
	// 		// Redis 포인트 증가
	// 		redissonClient.getAtomicLong(redisKey).addAndGet(amount);
	// 		// DB 포인트도 증가
	// 		user.updatePointAmount(amount);
	// 	}
	//
	// 	// 포인트 이력은 DB에도 항상 남긴다
	// 	PointHistories history = PointHistories.builder()
	// 		.user(user)
	// 		.amount(amount)
	// 		.pointType(pointType)
	// 		.build();
	//
	// 	pointHistoriesRepository.save(history);
	// }

	// 사용자 포인트 이력 최신순 조회
	@Transactional(readOnly = true)
	public Page<PointHistoriesResponse> findByUserId(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		Page<PointHistories> pointHistories = pointHistoriesRepository.findByUserId(userId, pageable);
		return pointHistories.map(PointHistoriesResponse::of);
	}

	// 사용자 ID로 사용자 조회 (없으면 예외 발생)
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, userId.toString()));
	}
}


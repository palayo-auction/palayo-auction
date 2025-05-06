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

	// Lua 리턴 코드 상수 정의
	private static final long LUA_RESULT_POINT_NOT_FOUND = -1;
	private static final long LUA_RESULT_INSUFFICIENT_POINT = -2;
	private static final long LUA_RESULT_PARSE_ERROR = -3;

	private static final String DECREASE_LUA_SCRIPT = """
        local current = redis.call('GET', KEYS[1])
        if not current or current == false then
            return -1
        end

        local currentNum = tonumber(current)
        local deduct = tonumber(ARGV[1])

        if not currentNum or not deduct then
            return -3
        end

        if currentNum < deduct then
            return -2
        end

        redis.call('DECRBY', KEYS[1], deduct)
        return 1
    """;

	@Transactional
	public void updatePoints(Long userId, int amount, PointType pointType) {
		String redisKey = "user:point:" + userId;
		User user = findUserById(userId);
		boolean redisUpdated = false;

		try {
			if (pointType == PointType.DECREASE) {
				Long result = redissonClient.getScript().eval(
					RScript.Mode.READ_WRITE,
					DECREASE_LUA_SCRIPT,
					RScript.ReturnType.INTEGER,
					Collections.singletonList(redisKey),
					amount
				);
				if (result == -1) throw new BaseException(ErrorCode.USER_POINT_NOT_FOUND, "Redis에 포인트 없음");
				if (result == -2) throw new BaseException(ErrorCode.INSUFFICIENT_POINT, "포인트 부족");
			} else {
				redissonClient.getAtomicLong(redisKey).addAndGet(amount);
			}

			redisUpdated = true;

			int signedAmount = switch (pointType) {
				case DECREASE -> -amount;
				case RECHARGE, REFUNDED, INCREASE -> amount;
			};
			user.updatePointAmount(signedAmount);

			pointHistoriesRepository.save(PointHistories.builder()
				.user(user)
				.amount(amount)
				.pointType(pointType)
				.build()
			);

		} catch (Exception e) {
			// Redis 롤백
			if (redisUpdated) {
				int rollbackAmount = (pointType == PointType.DECREASE) ? amount : -amount;
				redissonClient.getAtomicLong(redisKey).addAndGet(rollbackAmount);
			}
			throw e;
		}
	}

	// 정합성 검증: Redis와 DB 포인트 비교
	public void validatePointSync(Long userId) {
		String redisKey = "user:point:" + userId;
		long redisPoint = redissonClient.getAtomicLong(redisKey).get();

		long dbPoint = userRepository.findById(userId)
			.map(User::getPointAmount)
			.orElse(0);

		if (redisPoint != dbPoint) {
			System.out.printf("포인트 불일치: userId=%d, redis=%d, db=%d%n", userId, redisPoint, dbPoint);
			// 또는 log.warn(...) 사용 가능
		}
	}

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
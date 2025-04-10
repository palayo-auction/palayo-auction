package com.example.palayo.domain.pointhistory.service;

import com.example.palayo.domain.pointhistory.entity.PointHistories;
import com.example.palayo.domain.pointhistory.repository.PointHistoryRepository;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void chargePoints(Long userId, int amount) {
        userRepository.findById(userId).ifPresent(user -> {
            user.updatePointAmount(amount);
            userRepository.save(user);

            PointHistories history = PointHistories.builder()
                    .user(user)
                    .amount(amount)
                    .pointType(PointType.RECHARGE)
                    .build();

            pointHistoryRepository.save(history);
        });
    }
}


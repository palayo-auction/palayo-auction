package com.example.palayo.domain.pointhistory.service;

import com.example.palayo.domain.pointhistory.dto.PointHistoriesResponse;
import com.example.palayo.domain.pointhistory.entity.PointHistories;
import com.example.palayo.domain.pointhistory.repository.PointHistoriesRepository;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoriesService {

    private final UserRepository userRepository;
    private final PointHistoriesRepository pointHistoriesRepository;

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

            pointHistoriesRepository.save(history);
        });
    }

    public Page<PointHistoriesResponse> findByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<PointHistories> pointHistories = pointHistoriesRepository.findByUserId(userId, pageable);

        return pointHistories.map(PointHistoriesResponse::of);
    }
}


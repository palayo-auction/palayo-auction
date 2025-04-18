package com.example.palayo.domain.pointhistory.mongo.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.pointhistory.mongo.document.PointHistoryDocument;
import com.example.palayo.domain.pointhistory.mongo.dto.PointHistoryResponse;
import com.example.palayo.domain.pointhistory.mongo.repository.PointHistoryRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.enums.PointType;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    public void updatePointHistory(Long userId, int amount, PointType pointType) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, null)
        );

        if(pointType.equals(PointType.DECREASE) && user.getPointAmount() < amount) {
            throw new BaseException(ErrorCode.INSUFFICIENT_POINT, null);
        }

        user.updatePointAmount(amount);

        PointHistoryDocument pointHistory = PointHistoryDocument.builder()
                .userId(userId)
                .amount(amount)
                .pointType(pointType)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    public Page<PointHistoryResponse> getPointHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PointHistoryDocument> pointHistory = pointHistoryRepository.findByUserId(userId, pageable);
        return pointHistory.map(PointHistoryResponse::of);
    }
}

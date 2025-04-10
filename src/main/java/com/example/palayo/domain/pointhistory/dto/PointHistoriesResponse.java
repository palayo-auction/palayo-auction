package com.example.palayo.domain.pointhistory.dto;

import com.example.palayo.domain.pointhistory.entity.PointHistories;
import com.example.palayo.domain.user.enums.PointType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistoriesResponse {
    private Long id;
    private Long userId;
    private int amount;
    private PointType pointType;
    private LocalDateTime createdAt;

    public static PointHistoriesResponse of(PointHistories histories) {
        return new PointHistoriesResponse(
                histories.getId(),
                histories.getUser().getId(),
                histories.getAmount(),
                histories.getPointType(),
                histories.getCreatedAt()
        );
    }
}

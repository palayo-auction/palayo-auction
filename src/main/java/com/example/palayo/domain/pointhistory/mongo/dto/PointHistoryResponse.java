package com.example.palayo.domain.pointhistory.mongo.dto;

import com.example.palayo.domain.pointhistory.mongo.document.PointHistoryDocument;
import com.example.palayo.domain.user.enums.PointType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistoryResponse {
    private String id;
    private Long userId;
    private int amount;
    private PointType pointType;
    private LocalDateTime createdAt;

    public static PointHistoryResponse of(PointHistoryDocument document) {
        return new PointHistoryResponse(
                document.getId(),
                document.getUserId(),
                document.getAmount(),
                document.getPointType(),
                document.getCreatedAt()
        );
    }
}

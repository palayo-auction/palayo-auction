package com.example.palayo.domain.pointhistory.mongo.document;

import com.example.palayo.domain.user.enums.PointType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "point_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PointHistoryDocument {
    @Id
    private String id;

    private Long userId;

    private int amount;

    private PointType pointType;

    @CreatedDate
    private LocalDateTime createdAt;
}

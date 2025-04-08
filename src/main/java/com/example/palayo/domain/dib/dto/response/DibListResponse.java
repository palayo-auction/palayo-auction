package com.example.palayo.domain.dib.dto.response;

import com.example.palayo.domain.dib.entity.Dib;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DibListResponse {
    private final Long dibId;
    private final Long userId;
    private final Long auctionId;

    public DibListResponse(Long dibId, Long userId, Long auctionId) {
        this.dibId = dibId;
        this.userId = userId;
        this.auctionId = auctionId;
    }

    public static DibListResponse of(Dib dib){
        return DibListResponse.builder()
                .dibId(dib.getId())
                .userId(dib.getUser().getId())
                .auctionId(dib.getAuction().getId())
                .build();
    }
}

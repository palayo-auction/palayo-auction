package com.example.palayo.domain.dib.dto.response;

import com.example.palayo.domain.dib.entity.Dib;
import lombok.Getter;


@Getter
public class DibResponse {

    private final Long dibId;
    private final Long userId;
    private final Long auctionId;

    public DibResponse(Long dibId, Long userId, Long auctionId){
        this.dibId = dibId;
        this.userId = userId;
        this.auctionId = auctionId;
    }

    public static DibResponse of(Dib dib) {
        return new DibResponse(
                dib.getId(),
                dib.getUser().getId(),
                dib.getAuction().getId()
        );
    }

}

package com.example.palayo.domain.item.enums;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;

import java.util.Arrays;

public enum ItemStatus {
    UNDER_REVIEW,      // 검토 중 경매 등록전 수정 가능
    ON_AUCTION,        // 경매 진행 중 수정 불가능
    AUCTION_SUCCESS,   // 낙찰 완료 수정 불가능
    AUCTION_FAILED;    // 유찰 수정 가능

    public static ItemStatus of(String itemStatus){
        return Arrays.stream(ItemStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(itemStatus))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.STATUS_NOT_FOUND, itemStatus));
    }
}

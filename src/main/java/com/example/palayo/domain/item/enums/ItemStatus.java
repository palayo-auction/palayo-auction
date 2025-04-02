package com.example.palayo.domain.item.enums;

public enum ItemStatus {
    UNDER_REVIEW,      // 검토 중 경매 등록전 수정 가능
    ON_AUCTION,        // 경매 진행 중 수정 불가능
    AUCTION_SUCCESS,   // 낙찰 완료 수정 불가능
    AUCTION_FAILED    // 유찰 수정 가능
}

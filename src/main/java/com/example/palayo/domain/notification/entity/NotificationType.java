package com.example.palayo.domain.notification.entity;

//Topic을 구현하여 알림을 처리하기 위해 간단하게 생성 아직 사용 X -> 팀원과 논의후 결정

public enum NotificationType {
    BID_OVERBID,
    AUCTION_NEAR_END,
    AUCTION_NEAR_START,
    DIBS_AUCTION_START,
    DIBS_AUCTION_END,
    BID_SUCCESS,
    BID_FAIL
}

package com.example.palayo.domain.notification.entity;

//Topic을 구현하여 알림을 처리하기 위해 간단하게 생성 아직 사용 X -> 팀원과 논의후 결정

public enum NotificationType {
    AUCTION_SOON_END,
    DIBS_AUCTION_SOON_START,
    DIBS_AUCTION_SOON_END,
    MY_AUCTION_SOON_START,
    MY_AUCTION_SOON_END,
    AUCTION_WON,
    HIGHER_BID_PLACED
}

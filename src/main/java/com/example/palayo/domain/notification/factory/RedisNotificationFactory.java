package com.example.palayo.domain.notification.factory;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class RedisNotificationFactory {

    public RedisNotification dibAuctionStart(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("찜한 경매가 곧 시작됩니다!").body(" 경매가 5분 뒤 시작돼요!").data(Map.of("auctionId", auction.getId().toString())).type("STARTED").scheduledAt(auction.getStartedAt().minusMinutes(5)).build();
    }

    public RedisNotification dibAuctionEnd(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("찜한 경매가 곧 마감됩니다!").body(" 경매가 5분 뒤 마감돼요!").data(Map.of("auctionId", auction.getId().toString())).type("EXPIRED").scheduledAt(auction.getExpiredAt().minusMinutes(5)).build();
    }

    public RedisNotification myAuctionStart(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("등록한 경매가 곧 시작됩니다!").body(" 경매가 5분 뒤 시작돼요!").data(Map.of("auctionId", auction.getId().toString())).type("STARTED").scheduledAt(auction.getStartedAt().minusMinutes(5)).build();
    }

    public RedisNotification myAuctionEnd(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("등록한 경매가 곧 마감됩니다!").body(" 경매가 5분 뒤 마감돼요!").data(Map.of("auctionId", auction.getId().toString())).type("EXPIRED").scheduledAt(auction.getExpiredAt().minusMinutes(5)).build();
    }

    public RedisNotification bidOutbid(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("누군가 더 높은 금액으로 입찰했어요!").body(" 경매에 새로운 입찰이 등록되었어요.").data(Map.of("auctionId", auction.getId().toString())).type("OUTBID").scheduledAt(LocalDateTime.now()) // 즉시 전송
                .build();
    }

    public RedisNotification bidWin(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("🎉 입찰하신 경매에 낙찰되었어요!").body(" 경매에 낙찰되었어요! 확인해보세요.").data(Map.of("auctionId", auction.getId().toString())).type("WIN").scheduledAt(LocalDateTime.now()) // 즉시 전송
                .build();
    }

    public RedisNotification bidFail(User user, Auction auction) {
        return RedisNotification.builder().userId(user.getId()).title("아쉽지만 경매에 유찰되었어요").body(" 경매가 유찰되었어요. 다음 기회를 노려보세요.").data(Map.of("auctionId", auction.getId().toString())).type("FAIL").scheduledAt(LocalDateTime.now()) // 즉시 전송
                .build();
    }
}

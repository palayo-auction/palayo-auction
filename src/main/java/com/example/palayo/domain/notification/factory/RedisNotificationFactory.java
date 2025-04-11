package com.example.palayo.domain.notification.factory;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisNotificationFactory {

    public RedisNotification buildNotification(User user, Auction auction, String title, String body, String type, LocalDateTime scheduledAt) {
        return RedisNotification.builder()
                .userId(user.getId())
                .title(title)
                .body(body)
                .type(type)
                .data(Map.of("auctionId", auction.getId().toString()))
                .scheduledAt(scheduledAt)
                .build();
    }

    public List<RedisNotification> buildNotifications(List<User> users, Auction auction, String title, String body, String type, LocalDateTime scheduledAt) {
        return users.stream()
                .map(user -> buildNotification(user, auction, title, body, type, scheduledAt))
                .toList();
    }

    // 각각의 알림 메서드 (단일 유저)
    public RedisNotification bidEnd(User user, Auction auction) {
        return buildNotification(user, auction, "🔥 낙찰 기회! 경매 마감까지 5분 남았습니다!", "[" + auction.getItem().getName() + "] 경매가 곧 마감됩니다. 마지막 찬스를 놓치지 마세요!", "ENDING_SOON", auction.getExpiredAt().minusMinutes(5));
    }

    public RedisNotification bidOutbid(User previousTopBidder, Auction auction) {
        return buildNotification(previousTopBidder, auction, "입찰가가 상회되었습니다!", "[" + auction.getItem().getName() + "] 경매에 다른 사용자가 더 높은 금액으로 입찰했습니다.", "OUTBID", LocalDateTime.now());
    }

    public RedisNotification bidWin(User user, Auction auction) {
        return buildNotification(user, auction, "🎉 입찰하신 경매에 낙찰되었어요!", " 경매에 낙찰되었어요! 확인해보세요.", "WIN", LocalDateTime.now());
    }

    public RedisNotification bidFail(User user, Auction auction) {
        return buildNotification(user, auction, "아쉽지만 경매에 유찰되었어요", " 경매가 유찰되었어요. 다음 기회를 노려보세요.", "FAIL", LocalDateTime.now());
    }

    public RedisNotification myAuctionStart(User user, Auction auction) {
        return buildNotification(user, auction, "내 경매가 곧 시작됩니다.", " 경매가 5분 뒤 시작돼요!", "STARTED", LocalDateTime.now());
    }

    public RedisNotification myAuctionEnd(User user, Auction auction) {
        return buildNotification(user, auction, "내 경매가 곧 마감됩니다.", " 경매가 5분 뒤 마감돼요!", "EXPIRED", LocalDateTime.now());
    }

    // 각각의 알림 메서드 (여러 유저)
    public List<RedisNotification> dibAuctionStart(List<User> users, Auction auction) {
        return buildNotifications(users, auction, "찜한 경매가 곧 시작됩니다!", " 경매가 5분 뒤 시작돼요!", "STARTED", auction.getStartedAt().minusMinutes(5));
    }

    public List<RedisNotification> dibAuctionEnd(List<User> users, Auction auction) {
        return buildNotifications(users, auction, "찜한 경매가 곧 마감됩니다!", " 경매가 5분 뒤 마감돼요!", "EXPIRED", auction.getExpiredAt().minusMinutes(5));
    }



}

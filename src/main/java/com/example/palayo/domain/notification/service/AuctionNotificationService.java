//package com.example.palayo.domain.notification.service;
//
//import com.example.palayo.domain.auction.entity.Auction;
//import com.example.palayo.domain.auction.repository.LikeRepository;
//import com.example.palayo.domain.auction.repository.ParticipationRepository;
//import com.example.palayo.domain.notification.job.AuctionJobScheduler;
//import com.example.palayo.domain.notification.model.NotificationType;
//import com.example.palayo.domain.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Service
//public class AuctionNotificationService {
//
//    private final AuctionJobScheduler auctionJobScheduler;
//    private final ParticipationRepository participationRepository;
//    private final LikeRepository likeRepository;
//
//    /**
//     * 경매 관련 알림 예약 처리 (경매 생성 시 호출)
//     */
//    public void scheduleAuctionNotification(Auction auction) {
//        scheduleParticipantNotifications(auction);
//        scheduleLikedUserNotifications(auction);
//        scheduleSellerNotifications(auction);
//    }
//
//    private void scheduleParticipantNotifications(Auction auction) {
//        List<User> participants = participationRepository.findUsersByAuctionId(auction.getId());
//
//        ZonedDateTime notifyAt = auction.getEndAt().minusMinutes(5);
//
//        for (User user : participants) {
//            auctionJobScheduler.scheduleNotificationJob(
//                    user.getId(),
//                    NotificationType.AUCTION_SOON_END,
//                    "참여한 경매 마감 임박!",
//                    "5분 후 경매가 종료됩니다.",
//                    notifyAt,
//                    Map.of("auctionId", auction.getId().toString())
//            );
//        }
//    }
//
//    private void scheduleLikedUserNotifications(Auction auction) {
//        List<User> likedUsers = likeRepository.findUsersByAuctionId(auction.getId());
//
//        ZonedDateTime startNotifyAt = auction.getStartAt().minusMinutes(5);
//        ZonedDateTime endNotifyAt = auction.getEndAt().minusMinutes(5);
//
//        for (User user : likedUsers) {
//            auctionJobScheduler.scheduleNotificationJob(
//                    user.getId(),
//                    NotificationType.LIKED_AUCTION_SOON_START,
//                    "찜한 경매 시작 임박!",
//                    "5분 후 경매가 시작됩니다.",
//                    startNotifyAt,
//                    Map.of("auctionId", auction.getId().toString())
//            );
//
//            auctionJobScheduler.scheduleNotificationJob(
//                    user.getId(),
//                    NotificationType.LIKED_AUCTION_SOON_END,
//                    "찜한 경매 마감 임박!",
//                    "5분 후 경매가 종료됩니다.",
//                    endNotifyAt,
//                    Map.of("auctionId", auction.getId().toString())
//            );
//        }
//    }
//
//    private void scheduleSellerNotifications(Auction auction) {
//        User seller = auction.getSeller();
//
//        ZonedDateTime startNotifyAt = auction.getStartAt().minusMinutes(5);
//        ZonedDateTime endNotifyAt = auction.getEndAt().minusMinutes(5);
//
//        auctionJobScheduler.scheduleNotificationJob(
//                seller.getId(),
//                NotificationType.OWN_AUCTION_SOON_START,
//                "내 경매 시작 임박!",
//                "5분 후 등록한 경매가 시작됩니다.",
//                startNotifyAt,
//                Map.of("auctionId", auction.getId().toString())
//        );
//
//        auctionJobScheduler.scheduleNotificationJob(
//                seller.getId(),
//                NotificationType.OWN_AUCTION_SOON_END,
//                "내 경매 마감 임박!",
//                "5분 후 등록한 경매가 종료됩니다.",
//                endNotifyAt,
//                Map.of("auctionId", auction.getId().toString())
//        );
//    }
//}

// package com.example.palayo.auction.service;
//
// import com.example.palayo.common.dto.AuthUser;
// import com.example.palayo.common.exception.BaseException;
// import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
// import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
// import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
// import com.example.palayo.domain.auction.dto.response.AuctionResponse;
// import com.example.palayo.domain.auction.entity.Auction;
// import com.example.palayo.domain.auction.enums.AuctionStatus;
// import com.example.palayo.domain.auction.repository.AuctionRepository;
// import com.example.palayo.domain.auction.service.AuctionServiceHelper;
// import com.example.palayo.domain.auction.util.AuctionValidator;
// import com.example.palayo.domain.item.entity.Item;
// import com.example.palayo.domain.item.enums.Category;
// import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
// import com.example.palayo.domain.notification.redis.RedisNotification;
// import com.example.palayo.domain.notification.service.NotificationService;
// import com.example.palayo.domain.user.entity.User;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.*;
//
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.BDDMockito.*;
//
// @ExtendWith(MockitoExtension.class)
// class AuctionServiceTest {
//
// 	@InjectMocks
// 	private AuctionService auctionService;
//
// 	@Mock private AuctionRepository auctionRepository;
// 	@Mock private AuctionValidator auctionValidator;
// 	@Mock private AuctionServiceHelper auctionServiceHelper;
// 	@Mock private NotificationService notificationService;
// 	@Mock private RedisNotificationFactory redisNotificationFactory;
//
// 	private AuthUser authUser;
// 	private User seller;
// 	private Item item;
//
// 	@BeforeEach
// 	void setUp() {
// 		seller = User.of("test@email.com", "password", "nickname");
// 		authUser = new AuthUser(1L, "test@email.com");
// 		item = Item.of("Title", "Desc", Category.ART, seller);
// 	}
//
// 	@Test
// 	@DisplayName("즉시 시작 경매 생성")
// 	void saveAuction_instantStart() {
// 		CreateAuctionRequest request = CreateAuctionRequest.builder()
// 			.itemId(1L)
// 			.startingPrice(1000)
// 			.buyoutPrice(5000)
// 			.bidIncrement(100)
// 			.isInstantStart(true)
// 			.expiredAt(LocalDateTime.now().plusDays(1))
// 			.build();
//
// 		Auction auction = Auction.of(item, 1000, 5000, 100, LocalDateTime.now(), request.getExpiredAt());
// 		auction.markAsActive();
// 		auction.updateCurrentPrice(1000);
//
// 		given(auctionValidator.validateAuctionCreation(request, authUser)).willReturn(item);
// 		given(auctionRepository.save(any())).willReturn(auction);
// 		given(redisNotificationFactory.myAuctionStart(any(), any())).willReturn(mock(RedisNotification.class));
// 		given(redisNotificationFactory.myAuctionEnd(any(), any())).willReturn(mock(RedisNotification.class));
//
// 		AuctionResponse response = auctionService.saveAuction(authUser, request);
//
// 		assertThat(response).isNotNull();
// 		then(notificationService).should(times(2)).saveNotification(any());
// 	}
//
// 	@Test
// 	@DisplayName("예약 시작 경매 생성")
// 	void saveAuction_scheduledStart() {
// 		CreateAuctionRequest request = CreateAuctionRequest.builder()
// 			.itemId(1L)
// 			.startingPrice(2000)
// 			.buyoutPrice(6000)
// 			.bidIncrement(200)
// 			.isInstantStart(false)
// 			.startedAt(LocalDateTime.now().plusHours(1))
// 			.expiredAt(LocalDateTime.now().plusDays(1))
// 			.build();
//
// 		Auction auction = Auction.of(item, 2000, 6000, 200, request.getStartedAt(), request.getExpiredAt());
// 		auction.markAsReady();
// 		auction.updateCurrentPrice(2000);
//
// 		given(auctionValidator.validateAuctionCreation(request, authUser)).willReturn(item);
// 		given(auctionRepository.save(any())).willReturn(auction);
// 		given(redisNotificationFactory.myAuctionStart(any(), any())).willReturn(mock(RedisNotification.class));
// 		given(redisNotificationFactory.myAuctionEnd(any(), any())).willReturn(mock(RedisNotification.class));
//
// 		AuctionResponse response = auctionService.saveAuction(authUser, request);
//
// 		assertThat(response).isNotNull();
// 		then(notificationService).should(times(2)).saveNotification(any());
// 	}
//
// 	@Test
// 	@DisplayName("경매 상태 업데이트 성공")
// 	void updateAuctionStatus() {
// 		Auction auction = mock(Auction.class);
// 		given(auctionServiceHelper.updateStatus(auction)).willReturn(true);
// 		boolean result = auctionService.updateAuctionStatus(auction);
// 		assertThat(result).isTrue();
// 	}
//
// 	@Test
// 	@DisplayName("낙찰자 지정 성공")
// 	void assignWinningBidder() {
// 		Auction auction = mock(Auction.class);
// 		given(auctionServiceHelper.assignWinningBidder(auction)).willReturn(true);
// 		boolean result = auctionService.assignWinningBidder(auction);
// 		assertThat(result).isTrue();
// 	}
//
// 	@Test
// 	@DisplayName("진행 중인 경매 목록 조회")
// 	void getAuctions_success() {
// 		Auction auction = Auction.of(item, 1000, 3000, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
// 		auction.markAsActive();
// 		auction.updateCurrentPrice(1000);
// 		Page<Auction> page = new PageImpl<>(List.of(auction));
// 		given(auctionRepository.findAllByStatusIn(any(), any())).willReturn(page);
//
// 		Page<AuctionListResponse> result = auctionService.getAuctions(0, 10);
//
// 		assertThat(result.getContent()).hasSize(1);
// 	}
//
// 	@Test
// 	@DisplayName("특정 경매 상세 조회 성공")
// 	void getAuction_success() {
// 		Auction auction = Auction.of(item, 1000, 3000, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
// 		auction.markAsActive();
// 		given(auctionRepository.findByIdAndStatusIn(any(), any())).willReturn(Optional.of(auction));
//
// 		AuctionDetailResponse result = auctionService.getAuction(1L);
//
// 		assertThat(result).isNotNull();
// 	}
//
// 	@Test
// 	@DisplayName("특정 경매 상세 조회 실패 - 없음")
// 	void getAuction_notFound() {
// 		given(auctionRepository.findByIdAndStatusIn(any(), any())).willReturn(Optional.empty());
// 		assertThrows(BaseException.class, () -> auctionService.getAuction(1L));
// 	}
//
// 	@Test
// 	@DisplayName("내 경매 목록 조회")
// 	void getMyAuctions_success() {
// 		Auction auction = Auction.of(item, 1000, 3000, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
// 		auction.markAsActive();
// 		auction.updateCurrentPrice(1000);
// 		Page<Auction> page = new PageImpl<>(List.of(auction));
//
// 		given(auctionRepository.findAllByItemSellerIdAndStatusIn(eq(authUser.getUserId()), any(), any()))
// 			.willReturn(page);
//
// 		Page<AuctionListResponse> result = auctionService.getMyAuctions(authUser, 0, 10);
//
// 		assertThat(result.getContent()).hasSize(1);
// 	}
//
// 	@Test
// 	@DisplayName("내 경매 상세 조회 성공")
// 	void getMyAuction_success() {
// 		Auction auction = Auction.of(item, 1000, 3000, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
// 		auction.markAsActive();
// 		given(auctionRepository.findByIdAndStatusIn(any(), any())).willReturn(Optional.of(auction));
// 		willDoNothing().given(auctionServiceHelper).validateOwnership(any(), any());
// 		given(auctionServiceHelper.getWinningInfoIfPresent(any())).willReturn(null);
//
// 		AuctionDetailResponse result = auctionService.getMyAuction(authUser, 1L);
//
// 		assertThat(result).isNotNull();
// 	}
//
// 	@Test
// 	@DisplayName("내 경매 상세 조회 실패 - 없음")
// 	void getMyAuction_notFound() {
// 		given(auctionRepository.findByIdAndStatusIn(any(), any())).willReturn(Optional.empty());
// 		assertThrows(BaseException.class, () -> auctionService.getMyAuction(authUser, 1L));
// 	}
//
// 	@Test
// 	@DisplayName("경매 삭제 성공")
// 	void deleteAuction_success() {
// 		Auction auction = Auction.of(item, 1000, 3000, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
// 		given(auctionRepository.findById(1L)).willReturn(Optional.of(auction));
// 		willDoNothing().given(auctionServiceHelper).validateOwnership(any(), any());
// 		willDoNothing().given(auctionServiceHelper).validateDeletableAuction(any());
//
// 		auctionService.deleteAuction(authUser, 1L);
//
// 		assertThat(auction.getStatus()).isEqualTo(AuctionStatus.DELETED);
// 	}
//
// 	@Test
// 	@DisplayName("경매 삭제 실패 - 없음")
// 	void deleteAuction_notFound() {
// 		given(auctionRepository.findById(any())).willReturn(Optional.empty());
// 		assertThrows(BaseException.class, () -> auctionService.deleteAuction(authUser, 1L));
// 	}
// }
package com.example.palayo.auction.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auction.dto.response.AuctionResponse;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.auction.service.AuctionService;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuctionServiceTest {

	@Autowired private AuctionService auctionService;
	@Autowired private AuctionRepository auctionRepository;
	@Autowired private ItemRepository itemRepository;
	@Autowired private UserRepository userRepository;

	private User testUser;
	private Item testItem;
	private AuthUser authUser;

	@BeforeEach
	void setUp() {
		// ✅ 고유한 이메일과 닉네임 생성
		String uniqueEmail = "test+" + UUID.randomUUID() + "@email.com";
		String uniqueNickname = "tester_" + UUID.randomUUID(); // ✅ 유니크 닉네임

		testUser = userRepository.save(User.of(uniqueEmail, "encodedPassword123!", uniqueNickname));

		testItem = createInstance(Item.class);
		setField(testItem, "name", "Test Item");
		setField(testItem, "content", "Test Content");
		setField(testItem, "category", Category.FASHION);
		setField(testItem, "seller", testUser);
		testItem = itemRepository.save(testItem);

		authUser = createInstance(AuthUser.class);
		setField(authUser, "userId", testUser.getId());
	}

	private CreateAuctionRequest buildAuctionRequest() {
		CreateAuctionRequest request = new CreateAuctionRequest();
		setField(request, "itemId", testItem.getId());
		setField(request, "startingPrice", 1000);
		setField(request, "buyoutPrice", 5000);
		setField(request, "bidIncrement", 100);
		setField(request, "startedAt", LocalDateTime.now().plusMinutes(1));
		setField(request, "expiredAt", LocalDateTime.now().plusHours(1));
		return request;
	}

	private void setField(Object obj, String fieldName, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("경매 생성 성공")
	void saveAuction_success() {
		AuctionResponse response = auctionService.saveAuction(authUser, buildAuctionRequest());
		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo(AuctionStatus.READY.name());
	}

	@Test
	@DisplayName("진행중인 경매 전체 조회")
	void getAuctions_success() {
		auctionService.saveAuction(authUser, buildAuctionRequest());
		Page<AuctionListResponse> result = auctionService.getAuctions(0, 10);
		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("내가 등록한 경매 목록 조회")
	void getMyAuctions_success() {
		auctionService.saveAuction(authUser, buildAuctionRequest());
		Page<AuctionListResponse> result = auctionService.getMyAuctions(authUser, 0, 10);
		assertThat(result).isNotEmpty();
	}

	@Test
	@DisplayName("내가 등록한 경매 단건 조회")
	void getMyAuction_success() {
		AuctionResponse response = auctionService.saveAuction(authUser, buildAuctionRequest());
		AuctionDetailResponse result = auctionService.getMyAuction(authUser, response.auctionId());
		assertThat(result.getAuctionId()).isEqualTo(response.auctionId());
		assertThat(result.getSellerNickname()).isEqualTo(testUser.getNickname());
	}

	@Test
	@DisplayName("진행 중인 경매 단건 조회")
	void getAuction_success() {
		AuctionResponse response = auctionService.saveAuction(authUser, buildAuctionRequest());
		AuctionDetailResponse result = auctionService.getAuction(response.auctionId());
		assertThat(result.getAuctionId()).isEqualTo(response.auctionId());
	}

	@Test
	@DisplayName("경매 삭제 테스트")
	void deleteAuction_success() {
		AuctionResponse response = auctionService.saveAuction(authUser, buildAuctionRequest());
		auctionService.deleteAuction(authUser, response.auctionId());

		Auction deleted = auctionRepository.findById(response.auctionId()).orElseThrow();
		assertThat(deleted.getStatus()).isEqualTo(AuctionStatus.DELETED);
	}

	private <T> T createInstance(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("객체 생성 실패: " + clazz.getSimpleName(), e);
		}
	}
}
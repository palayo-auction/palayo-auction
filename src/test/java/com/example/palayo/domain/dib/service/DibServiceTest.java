package com.example.palayo.domain.dib.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.repository.DibRepository;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DibServiceTest {

    @Mock
    private DibRepository dibRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DibService dibService;

    private User user;
    private Auction auction;
    private AuthUser authUser;
    private Item item;

    @BeforeEach
    void setUp() {
        // User 생성
        user = User.of(
                "test@email.com",
                "password123",
                "nickname",
                1000
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        // Category 설정
        Category category = Category.ART;

        // Item 생성 (★ seller 연결)
        item = Item.of(
                "멋진 상품",
                "멋진 상품 설명",
                category
        );

        // Auction 생성
        auction = Auction.of(
                item,
                1000,         //시작가
                10000,        // 즉시 낙찰가
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        ReflectionTestUtils.setField(auction, "id", 1L);

        // AuthUser 생성
        authUser = new AuthUser(user.getId(), "ROLE_USER");
    }


    @Test
    @DisplayName("경매 찜 성공")
    void 경매찜에_성공한다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.empty());

        // when
        assertDoesNotThrow(() -> dibService.dibAuction(authUser, 1L));

        // then
        verify(dibRepository, times(1)).save(any(Dib.class));
    }

    @Test
    @DisplayName("이미 찜한 경매일 경우 예외 발생")
    void 경매찜에_실패한다_이미찜한경우() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.of(new Dib()));

        // when, then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dibService.dibAuction(authUser, 1L));

        assertEquals("이미 찜한 경매입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("경매 찜 취소 성공")
    void 경매찜취소에_성공한다() {
        // given
        Dib dib = Dib.of(user, auction);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.of(dib));

        // when
        assertDoesNotThrow(() -> dibService.unDibAuction(authUser, 1L));

        // then
        verify(dibRepository, times(1)).delete(dib);
    }

    @Test
    @DisplayName("찜을 취소할 때 찜을 찾지 못하면 예외 발생")
    void 경매찜취소에_실패한다_찜없음() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.empty());

        // when, then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dibService.unDibAuction(authUser, 1L));

        assertEquals("Dib not found", exception.getMessage());
    }

    @Test
    @DisplayName("내가 찜한 경매 리스트 페이징 조회 성공")
    void 내가찜한경매리스트_조회에_성공한다() {
        // given
        // PageRequest pageable = PageRequest.of(0, 10);
        Page<Dib> dibPage = new PageImpl<>(List.of(Dib.of(user, auction)));

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(dibRepository.findAllByUser(any(), any())).willReturn(dibPage);

        // when
        Response<Page<Dib>> response = dibService.getMyDibs(authUser, 0, 10);

        // then
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertEquals(1, response.getData().getTotalElements());
    }
}

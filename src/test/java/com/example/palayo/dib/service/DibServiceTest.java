package com.example.palayo.dib.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.dib.dto.response.DibListResponse;
import com.example.palayo.domain.dib.dto.response.DibResponse;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.repository.DibRepository;
import com.example.palayo.domain.dib.service.DibService;
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

    @Mock
    private User user;

    @Mock
    private Auction auction;

    @Mock
    private AuthUser authUser;

    @Mock
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password123", "kimchiman");
        ReflectionTestUtils.setField(user, "id", 1L);

        Category category = Category.ART;
        item = Item.of("멋진 상품", "멋진 상품 설명", category, user);

        auction = Auction.of(
                item,
                1000,
                10000,
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        ReflectionTestUtils.setField(auction, "id", 1L);

        authUser = new AuthUser(user.getId(), "ROLE_USER");
    }

    @Test
    @DisplayName("경매 찜 성공")
    void 경매찜에_성공한다() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.empty());
        given(dibRepository.save(any(Dib.class))).willAnswer(invocation -> {
            Dib dib = invocation.getArgument(0);
            ReflectionTestUtils.setField(dib, "id", 1L);
            return dib;
        });

        // when
        DibResponse result = dibService.dibAuction(authUser, 1L);

        // then
        assertNotNull(result);
        assertEquals(user.getId(), result.getUserId());
        verify(dibRepository, times(1)).save(any(Dib.class));
    }

    @Test
    @DisplayName("이미 찜한 경매일 경우 찜한 경매 삭제")
    void 경매찜취소에_이미찜한경우() {
        // given
        Dib dib = Dib.of(user, auction);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(auctionRepository.findById(anyLong())).willReturn(Optional.of(auction));
        given(dibRepository.findByAuctionAndUser(any(), any())).willReturn(Optional.of(dib));

        // when
        DibResponse result = dibService.dibAuction(authUser, 1L);

        // then
        assertNull(result); // 찜 취소 시 null 반환
        verify(dibRepository, times(1)).delete(any(Dib.class));
    }

    @Test
    @DisplayName("내가 찜한 경매 리스트 페이징 조회 성공")
    void 내가찜한경매리스트_조회에_성공한다() {
        // given
        Dib dib = Dib.of(user, auction);
        Page<Dib> dibPage = new PageImpl<>(
                List.of(dib),
                PageRequest.of(0, 10),
                1
        );

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(dibRepository.findAllByUser(any(), any())).willReturn(dibPage);

        // when
        Page<DibListResponse> page = dibService.getMyDibs(authUser, 0, 10);
        Response<List<DibListResponse>> response = Response.fromPage(page);

        // then
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertEquals(1, response.getData().size());
    }
}
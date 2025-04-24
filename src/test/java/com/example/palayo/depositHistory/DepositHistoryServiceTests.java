package com.example.palayo.depositHistory;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DepositHistoryServiceTests {

    @Mock
    private DepositHistoryRepository depositHistoryRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepositHistoryService depositHistoryService;

    private User user;
    private Auction auction;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password123", "nickname");
        ReflectionTestUtils.setField(user, "id", 1L);

        auction = Auction.of(null, 1000, 10000, 100, null, null);
        ReflectionTestUtils.setField(auction, "id", 1L);
    }

    @Test
    @DisplayName("보증금 이력 생성 성공")
    void createDepositHistory_success() {
        // given
        int depositAmount = 8000;

        given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(depositHistoryRepository.existsByAuctionIdAndUserId(auction.getId(), user.getId())).willReturn(false);
        given(depositHistoryRepository.save(any(DepositHistory.class)))
                .willAnswer(invocation -> {
                    DepositHistory saved = invocation.getArgument(0);
                    ReflectionTestUtils.setField(saved, "id", 1L);
                    return saved;
                });

        // when
        depositHistoryService.createDepositHistory(user.getId(), auction.getId(), depositAmount);

        // then
        verify(depositHistoryRepository).save(any(DepositHistory.class));
    }

    @Test
    @DisplayName("보증금 이력 생성 실패 - 이미 존재")
    void createDepositHistory_fail_alreadyExists() {
        // given
        given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(depositHistoryRepository.existsByAuctionIdAndUserId(auction.getId(), user.getId())).willReturn(true);

        // when & then
        assertThrows(BaseException.class, () ->
                depositHistoryService.createDepositHistory(user.getId(), auction.getId(), 8000)
        );
    }

    @Test
    @DisplayName("보증금 단건 조회 성공")
    void getDepositHistory_success() {
        // given
        DepositHistory depositHistory = new DepositHistory(auction, user, 8000, DepositStatus.PENDING);
        ReflectionTestUtils.setField(depositHistory, "id", 1L);

        given(depositHistoryRepository.findById(1L)).willReturn(Optional.of(depositHistory));

        // when
        DepositHistoryResponse response = depositHistoryService.getDepositHistory(1L);

        // then
        assertNotNull(response);
        assertEquals(8000, response.getDeposit());
        assertEquals(DepositStatus.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("보증금 단건 조회 실패 - 없음")
    void getDepositHistory_fail_notFound() {
        // given
        given(depositHistoryRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(BaseException.class, () -> depositHistoryService.getDepositHistory(1L));
    }

    @Test
    @DisplayName("보증금 이력 리스트 조회 성공")
    void getDepositHistoryList_success() {
        // given
        DepositHistory depositHistory = new DepositHistory(auction, user, 8000, DepositStatus.PENDING);
        ReflectionTestUtils.setField(depositHistory, "id", 1L);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(depositHistoryRepository.findByAuctionAndUser(auction, user, pageRequest))
                .willReturn(new PageImpl<>(List.of(depositHistory)));

        // when
        Page<DepositHistoryResponse> result = depositHistoryService.getDepositHistoryList(
                auction.getId(), 0, 10, new com.example.palayo.common.dto.AuthUser(user.getId(), "ROLE_USER")
        );

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(8000, result.getContent().get(0).getDeposit());
    }
}

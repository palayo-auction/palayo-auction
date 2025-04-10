package com.example.palayo.depositHistory;
import com.example.palayo.common.dto.AuthUser;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositHistoryServiceTest {

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
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password123", "nickname");
        ReflectionTestUtils.setField(user, "id", 1L);

        auction = Auction.of(null, 1000, 10000, 100, null, null);
        ReflectionTestUtils.setField(auction, "id", 1L);

        authUser = new AuthUser(user.getId(), "ROLE_USER");
    }

    @Test
    @DisplayName("보증금 이력 생성 성공")
    void depositHistoryCreationSuccess() {
        int depositAmount = 8000;

        // Auction, User 조회
        given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // 이미 보증금 이력 존재하지 않음
        given(depositHistoryRepository.existsByAuctionIdAndUserId(auction.getId(), user.getId())).willReturn(false);

        // 저장된 보증금 이력 설정
        given(depositHistoryRepository.save(any(DepositHistory.class))).willAnswer(invocation -> {
            DepositHistory saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        // 실행 및 예외가 발생하지 않음을 확인
        assertDoesNotThrow(() ->
                depositHistoryService.createDepositHistory(user.getId(), auction.getId(), depositAmount)
        );

        // 보증금 이력 저장 호출 확인
        verify(depositHistoryRepository, times(1)).save(any(DepositHistory.class));
    }

    @Test
    @DisplayName("보증금 이력 생성 실패 - 이미 존재함")
    void depositHistoryCreationFailureAlreadyExists() {
        int depositAmount = 8000;

        // Auction, User 조회
        given(auctionRepository.findById(auction.getId())).willReturn(Optional.of(auction));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // 이미 보증금 이력이 존재함
        given(depositHistoryRepository.existsByAuctionIdAndUserId(auction.getId(), user.getId())).willReturn(true);

        // 예외 발생 확인
        BaseException exception = assertThrows(BaseException.class, () ->
                depositHistoryService.createDepositHistory(user.getId(), auction.getId(), depositAmount)
        );

        assertEquals("이미 존재하는 보증금 이력입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("보증금 이력 조회 성공")
    void depositHistoryQuerySuccess() {
        int depositAmount = 8000;
        DepositHistory depositHistory = new DepositHistory(auction, user, depositAmount, DepositStatus.PENDING);
        ReflectionTestUtils.setField(depositHistory, "id", 1L);

        // DepositHistory 조회
        given(depositHistoryRepository.findById(1L)).willReturn(Optional.of(depositHistory));

        DepositHistoryResponse result = depositHistoryService.getDepositHistory(1L);

        assertNotNull(result);
        assertEquals(depositAmount, result.getDeposit());
        assertEquals(DepositStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("보증금 이력 조회 실패 - 존재하지 않음")
    void depositHistoryQueryFailureNotFound() {
        // DepositHistory가 존재하지 않음
        given(depositHistoryRepository.findById(1L)).willReturn(Optional.empty());

        // 예외 발생 확인
        BaseException exception = assertThrows(BaseException.class, () ->
                depositHistoryService.getDepositHistory(1L)
        );

        assertEquals("보증금 이력을 찾을 수 없습니다.", exception.getMessage());
    }
}
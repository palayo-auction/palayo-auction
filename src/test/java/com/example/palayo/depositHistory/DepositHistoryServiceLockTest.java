package com.example.palayo.depositHistory;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.deposithistory.entity.DepositHistory;
import com.example.palayo.domain.deposithistory.repository.DepositHistoryRepository;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositHistoryServiceLockTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepositHistoryRepository depositHistoryRepository;

    private DepositHistoryService depositHistoryService;

    @BeforeEach
    void setUp() {
        depositHistoryService = new DepositHistoryService(
                depositHistoryRepository,
                auctionRepository,
                userRepository,
                redissonClient
        );
    }

    // ✅ 락 획득 성공 및 저장까지 정상 작동
    @Test
    void testLockAcquisition_Success() throws InterruptedException {
        Long auctionId = 1L;
        Long userId = 1L;
        Auction auction = mock(Auction.class);
        User user = mock(User.class);

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId)).thenReturn(false);

        assertDoesNotThrow(() -> depositHistoryService.createDepositHistory(userId, auctionId, 100));

        verify(rLock).unlock();
        verify(depositHistoryRepository).save(any(DepositHistory.class));
    }

    // ❌ 락 획득 실패 시 예외 발생
    @Test
    void testLockAcquisition_Failure() throws InterruptedException {
        Long auctionId = 1L;
        Long userId = 1L;

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);

        assertThrows(BaseException.class, () -> depositHistoryService.createDepositHistory(userId, auctionId, 100));

        verify(rLock, never()).unlock();
    }

    // ❌ 락 획득 중 인터럽트 발생 시 예외 처리
    @Test
    void testLockAcquisition_InterruptedException() throws InterruptedException {
        Long auctionId = 1L;
        Long userId = 1L;

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenThrow(new InterruptedException());

        assertThrows(BaseException.class, () -> depositHistoryService.createDepositHistory(userId, auctionId, 100));

        verify(rLock, never()).unlock();
    }

    // ❌ 이미 보증금 납부 이력이 있는 경우 예외
    @Test
    void testDepositAlreadyExists() throws InterruptedException {
        Long auctionId = 1L;
        Long userId = 1L;
        Auction auction = mock(Auction.class);
        User user = mock(User.class);

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(depositHistoryRepository.existsByAuctionIdAndUserId(auctionId, userId)).thenReturn(true);

        assertThrows(BaseException.class, () -> depositHistoryService.createDepositHistory(userId, auctionId, 100));

        verify(rLock).unlock();
    }
}

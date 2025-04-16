package com.example.palayo.domain.notification;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.notification.entity.Notification;
import com.example.palayo.domain.notification.entity.NotificationHistory;
import com.example.palayo.domain.notification.enums.NotificationType;
import com.example.palayo.domain.notification.redis.RedisNotification;
import com.example.palayo.domain.notification.repository.NotificationHistoryRepository;
import com.example.palayo.domain.notification.repository.NotificationRepository;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationHistoryRepository historyRepository;
    @Mock private RedisTemplate<String, RedisNotification> redisTemplate;
    @Mock private ValueOperations<String, RedisNotification> valueOps;

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password", "nickname");
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    void registerToken_성공() {
        // given
        String token = "sample-token";
        Notification notification = Notification.builder().user(user).token("old-token").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(notificationRepository.findByUser(user)).willReturn(Optional.of(notification));

        // when
        notificationService.registerToken(1L, token);

        // then
        assertThat(notification.getToken()).isEqualTo(token);
        then(notificationRepository).should(never()).save(any());
    }

    @Test
    void sendNotification_성공() throws Exception {
        // given
        String token = "sample-token";
        String title = "title";
        String body = "body";
        Map<String, String> data = Map.of("key", "value");

        given(notificationRepository.findByUser(user))
                .willReturn(Optional.of(Notification.builder().user(user).token(token).build()));

        try (MockedStatic<FirebaseMessaging> firebaseMockedStatic = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessagingMock = mock(FirebaseMessaging.class);
            firebaseMockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessagingMock);
            given(firebaseMessagingMock.send(any(Message.class))).willReturn("messageId");

            // when
            notificationService.sendNotification(user, NotificationType.TEST, title, body, data);

            // then
            then(historyRepository).should().save(any(NotificationHistory.class));
        }
    }

    @Test
    void sendNotification_토큰없음_실패() {
        // given
        given(notificationRepository.findByUser(user)).willReturn(Optional.empty());

        // when & then
        BaseException ex = assertThrows(BaseException.class, () ->
                notificationService.sendNotification(user, NotificationType.TEST, "t", "b", new HashMap<>()));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FCM_TOKEN_NOT_FOUND);
    }

    @Test
    void reserveNotification_성공() {
        // given
        Map<String, String> data = Map.of("someKey", "someValue");
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(10);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        notificationService.reserveNotification(1L, NotificationType.TEST, "title", "body", data, scheduledTime);

        // then
        then(historyRepository).should().save(any(NotificationHistory.class));
    }

    @Test
    void processScheduledNotifications_성공() throws Exception {
        // given
        NotificationHistory history = NotificationHistory.builder()
                .user(user)
                .type(NotificationType.TEST)
                .title("Scheduled")
                .body("Body")
                .data(Map.of("auctionId", "1"))
                .scheduledAt(LocalDateTime.now().minusMinutes(1))
                .isSent(false)
                .build();

        Notification notification = Notification.builder().user(user).token("valid-token").build();

        given(historyRepository.findAllByIsSentFalseAndScheduledAtBefore(any()))
                .willReturn(List.of(history));
        given(notificationRepository.findByUser(user)).willReturn(Optional.of(notification));

        try (MockedStatic<FirebaseMessaging> firebaseMockedStatic = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessagingMock = mock(FirebaseMessaging.class);
            firebaseMockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessagingMock);
            given(firebaseMessagingMock.send(any(Message.class))).willReturn("id");

            // when
            notificationService.processScheduledNotifications();

            // then
            assertThat(history.isSent()).isTrue();
            then(historyRepository).should().saveAll(any());
        }
    }

    @Test
    void saveNotification_성공() {
        // given
        RedisNotification redisNotification = RedisNotification.builder()
                .userId(1L)
                .type("ALERT")
                .data(Map.of("auctionId", "99"))
                .build();

        String key = "notification:1:99:ALERT";
        given(redisTemplate.hasKey(key)).willReturn(true);
        given(redisTemplate.opsForValue()).willReturn(valueOps);

        // when
        notificationService.saveNotification(redisNotification);

        // then
        then(redisTemplate).should().delete(key);
        then(valueOps).should().set(key, redisNotification);
    }

    @Test
    void saveNotifications_여러개저장_성공() {
        // given
        RedisNotification n1 = RedisNotification.builder().userId(1L).type("T1").data(Map.of("auctionId", "1")).build();
        RedisNotification n2 = RedisNotification.builder().userId(2L).type("T2").data(Map.of("auctionId", "2")).build();
        given(redisTemplate.opsForValue()).willReturn(valueOps);

        // when
        notificationService.saveNotifications(List.of(n1, n2));

        // then
        then(redisTemplate).should(times(2)).opsForValue();
        then(valueOps).should(times(2)).set(anyString(), any(RedisNotification.class));
    }
}

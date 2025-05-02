package com.example.palayo.domain.dib.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.dib.dto.response.DibListResponse;
import com.example.palayo.domain.dib.dto.response.DibRedisCacheResponse;
import com.example.palayo.domain.dib.dto.response.DibResponse;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.repository.DibRepository;
import com.example.palayo.domain.notification.factory.RedisNotificationFactory;
import com.example.palayo.domain.notification.service.NotificationService;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DibService {
    private final DibRepository dibRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RedisNotificationFactory redisNotificationFactory;
    private final RedisTemplate<String, DibRedisCacheResponse> dibCacheRedisTemplate;

    private String getCacheKey(Long userId) {
        return "dib::" + userId;
    }

    @Transactional
    public DibResponse dibAuction(AuthUser authUser, Long auctionId) {
        User user = getUserOrThrow(authUser);
        Auction auction = getAuctionOrThrow(auctionId);
        Optional<Dib> existingDib = findDib(user, auction);

        if (existingDib.isPresent()) {
            dibRepository.delete(existingDib.get());
            return null;
        } else {
            Dib savedDib = dibRepository.save(Dib.of(user, auction));
            return DibResponse.of(savedDib);
        }
    }

    @Transactional(readOnly = true)
    public Page<DibListResponse> getMyDibs(AuthUser authUser, int page, int size) {
        User user = getUserOrThrow(authUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Dib> dibPage = dibRepository.findAllByUser(user, pageable);

        return dibPage.map(DibListResponse::of);
    }

    @Transactional(readOnly = true)
    public DibResponse getMyDib(AuthUser authUser, Long dibId) {
        Dib dib = dibRepository.findById(dibId)
                .orElseThrow(() -> new BaseException(ErrorCode.DIB_NOT_FOUND, dibId.toString()));

        if (!dib.getUser().getId().equals(authUser.getUserId())) {
            throw new BaseException(ErrorCode.DIB_FORBIDDEN, authUser.getUserId().toString());
        }

        return DibResponse.of(dib);
    }
//---------------------------------Redis----------------------------------------------------------------

    @Transactional
    public DibResponse toggleDib(AuthUser authUser, Long auctionId) {
        User user = getUserOrThrow(authUser);

        Auction auction = getAuctionOrThrow(auctionId);

        Optional<Dib> existingDib = findDib(user, auction);

        String key = getCacheKey(authUser.getUserId());

        if (existingDib.isPresent()) {
            dibRepository.delete(existingDib.get());
            dibCacheRedisTemplate.opsForSet().remove(key,
                    DibRedisCacheResponse.builder()
                            .dibId(existingDib.get().getId())
                            .userId(user.getId())
                            .auctionId(auction.getId())
                            .build());
            return null;
        } else {
            Dib dib = dibRepository.save(Dib.of(user, auction));

            DibRedisCacheResponse cacheDto = DibRedisCacheResponse.builder()
                    .dibId(dib.getId())
                    .userId(user.getId())
                    .auctionId(auction.getId())
                    .build();

            dibCacheRedisTemplate.opsForSet().add(key, cacheDto);
            dibCacheRedisTemplate.expire(key, Duration.ofDays(7));
            return DibResponse.of(dib);
        }
    }

    @Transactional(readOnly = true)
    public Page<DibListResponse> redisGetMyDibs(AuthUser authUser, int page, int size) {
        String key = getCacheKey(authUser.getUserId());
        Set<DibRedisCacheResponse> cachedDibs = dibCacheRedisTemplate.opsForSet().members(key);

        if (cachedDibs == null || cachedDibs.isEmpty()) {
            return Page.empty();
        }

        List<DibListResponse> dibList = cachedDibs.stream()
                .sorted((a, b) -> b.getDibId().compareTo(a.getDibId()))
                .skip((long) page * size)
                .limit(size)
                .map(dto -> new DibListResponse(dto.getDibId(), dto.getUserId(), dto.getAuctionId()))
                .toList();

        return new PageImpl<>(dibList, PageRequest.of(page, size), cachedDibs.size());
    }

    @Transactional(readOnly = true)
    public DibResponse redisGetMyDibById(AuthUser authUser, Long dibId) {
        String key = getCacheKey(authUser.getUserId());
        Set<DibRedisCacheResponse> cachedDibs = dibCacheRedisTemplate.opsForSet().members(key);

        if (cachedDibs == null || cachedDibs.isEmpty()) {
            throw new BaseException(ErrorCode.DIB_NOT_FOUND, authUser.getUserId().toString());
        }
        return cachedDibs.stream()
                .filter(dib -> dib.getDibId().equals(dibId))
                .findFirst()
                .map(dto -> new DibResponse(dto.getDibId(), dto.getUserId(), dto.getAuctionId()))
                .orElseThrow(() -> new BaseException(ErrorCode.DIB_NOT_FOUND, dibId.toString()));

    }

    private User getUserOrThrow(AuthUser authUser) {
        return userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getEmail()));
    }

    private Auction getAuctionOrThrow(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, auctionId.toString()));
    }

    private Optional<Dib> findDib(User user, Auction auction) {
        return dibRepository.findByAuctionAndUser(auction, user);
    }
}

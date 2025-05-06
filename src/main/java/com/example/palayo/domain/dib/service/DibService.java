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
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibService {
    private final DibRepository dibRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, DibRedisCacheResponse> dibCacheRedisTemplate;

    private String getCacheKey(Long userId, Long auctionId) {
        return "dib::" + userId + "::" + auctionId;
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

    @Transactional
    public DibResponse toggleDib(AuthUser authUser, Long auctionId) {
        User user = getUserOrThrow(authUser);
        Auction auction = getAuctionOrThrow(auctionId);
        Optional<Dib> existingDib = findDib(user, auction);

        String key = getCacheKey(user.getId(), auction.getId());

        if (existingDib.isPresent()) {
            dibRepository.delete(existingDib.get());
            dibCacheRedisTemplate.delete(key);
            return null;
        } else {
            Dib dib = dibRepository.save(Dib.of(user, auction));

            DibRedisCacheResponse cacheDto = DibRedisCacheResponse.builder()
                    .dibId(dib.getId())
                    .userId(user.getId())
                    .auctionId(auction.getId())
                    .build();

            dibCacheRedisTemplate.opsForValue().set(key, cacheDto);

            LocalDateTime now = LocalDateTime.now();
            Duration ttl = Duration.between(now, auction.getExpiredAt());

            if (!ttl.isNegative() && !ttl.isZero()) {
                dibCacheRedisTemplate.expire(key, ttl);
            } else {
                dibCacheRedisTemplate.expire(key, Duration.ofHours(1));
            }

            return DibResponse.of(dib);
        }
    }

    @Transactional(readOnly = true)
    public Page<DibListResponse> redisGetMyDibs(AuthUser authUser, int page, int size) {
        String pattern = "dib::" + authUser.getUserId() + "::*";

        List<String> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        var cursor = dibCacheRedisTemplate.scan(options);
        cursor.forEachRemaining(keys::add);

        List<DibRedisCacheResponse> cacheDtos = dibCacheRedisTemplate.opsForValue().multiGet(keys);

        List<DibListResponse> dibList = cacheDtos.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(DibRedisCacheResponse::getDibId).reversed())
                .skip((long) page * size)
                .limit(size)
                .map(dto -> new DibListResponse(dto.getDibId(), dto.getUserId(), dto.getAuctionId()))
                .collect(Collectors.toList());

        return new PageImpl<>(dibList, PageRequest.of(page, size), keys.size());
    }

    @Transactional(readOnly = true)
    public DibResponse redisGetMyDibById(AuthUser authUser, Long dibId) {
        String pattern = "dib::" + authUser.getUserId() + "::*";

        var cursor = dibCacheRedisTemplate.scan(ScanOptions.scanOptions().match(pattern).build());
        List<String> keys = new ArrayList<>();
        cursor.forEachRemaining(keys::add);

        List<DibRedisCacheResponse> cacheDtos = dibCacheRedisTemplate.opsForValue().multiGet(keys);

        return cacheDtos.stream()
                .filter(Objects::nonNull)
                .filter(dto -> dto.getDibId().equals(dibId))
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

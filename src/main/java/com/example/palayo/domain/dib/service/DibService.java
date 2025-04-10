package com.example.palayo.domain.dib.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.dib.dto.response.DibListResponse;
import com.example.palayo.domain.dib.dto.response.DibResponse;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.repository.DibRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DibService {
    private final DibRepository dibRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DibResponse dibAuction(AuthUser authUser, Long auctionId) {
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getEmail()));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BaseException(ErrorCode.AUCTION_NOT_FOUND, auctionId.toString()));

        Optional<Dib> existingDib = dibRepository.findByAuctionAndUser(auction, user);

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
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, authUser.getEmail()));

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
}

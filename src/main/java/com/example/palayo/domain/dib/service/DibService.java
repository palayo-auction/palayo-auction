package com.example.palayo.domain.dib.service;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.repository.DibRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DibService {
    private final DibRepository dibRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public void dibAuction(AuthUser authUser, Long auctionId) {
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        if (dibRepository.findByAuctionAndUser(auction,user).isPresent()){
            throw new IllegalArgumentException("이미 찜한 경매입니다.");
        }

        dibRepository.save(Dib.of(user, auction));
    }

    public void unDibAuction(AuthUser authUser, Long auctionId) {
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        Dib dib = dibRepository.findByAuctionAndUser(auction,user)
                .orElseThrow(() -> new IllegalArgumentException("Dib not found"));

        dibRepository.delete(dib);
    }

    public Response<Page<Dib>> getMyDibs(AuthUser authUser, int page, int size) {
        User user = userRepository.findById(authUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Dib> dibPage = dibRepository.findAllByUser(user, pageable);

        return Response.of(dibPage);
    }

}

package com.example.palayo.domain.dib.repository;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DibRepository extends JpaRepository<Dib, Long> {
    Optional<Dib> findByAuctionAndUser(Auction auction, User user);

    Page<Dib> findAllByUser(User user, Pageable pageable);

}

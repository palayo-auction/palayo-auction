package com.example.palayo.domain.dib.entity;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "dibs")
public class Dib {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Dib(User user, Auction auction) {
        this.user = user;
        this.auction = auction;
    }

    public static Dib of(User user, Auction auction) {
        return new Dib(user, auction);
    }
}

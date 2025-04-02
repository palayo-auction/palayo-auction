package com.example.palayo.domain.dib.entity;

import com.example.palayo.common.entity.BaseEntity;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "dibs")
public class Dib extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    private Dib(User user, Auction auction) {
        this.user = user;
        this.auction = auction;
    }

    public static Dib of(User user, Auction auction) {
        return new Dib(user, auction);
    }
}

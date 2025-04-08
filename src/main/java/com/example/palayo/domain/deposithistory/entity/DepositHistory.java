package com.example.palayo.domain.deposithistory.entity;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "deposit_histories")
public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long deposit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public DepositHistory(Auction auction, User user, Long deposit, DepositStatus status) {
        this.auction = auction;
        this.user = user;
        this.deposit = deposit;
        this.status = status;
    }

    public void setDeposit(Long deposit) {
        this.deposit = deposit;
    }

    public void setStatus(DepositStatus depositStatus) {
        this.status = depositStatus;
    }
    }



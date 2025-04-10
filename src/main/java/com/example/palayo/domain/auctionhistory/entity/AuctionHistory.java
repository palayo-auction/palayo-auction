package com.example.palayo.domain.auctionhistory.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.user.entity.User;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "auction_histories")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AuctionHistory {

	// 입찰 기록 ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 입찰 대상 경매
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;

	// 입찰자 (User)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id", nullable = false)
	private User bidder;

	// 입찰 금액
	@Column(nullable = false)
	private int bidPrice;

	// 입찰 생성 일시 (자동 설정)
	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	private AuctionHistory(Auction auction, User bidder, int bidPrice) {
		this.auction = auction;
		this.bidder = bidder;
		this.bidPrice = bidPrice;
	}

	public static AuctionHistory of(Auction auction, User bidder, int bidPrice) {
		return new AuctionHistory(auction, bidder, bidPrice);
	}
}

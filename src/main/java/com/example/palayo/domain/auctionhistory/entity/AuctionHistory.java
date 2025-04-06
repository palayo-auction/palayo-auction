package com.example.palayo.domain.auctionhistory.entity;

import java.time.LocalDateTime;

import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import com.example.palayo.domain.auction.entity.Auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "auction_histories")
public class AuctionHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id", nullable = false)
	private User buyer;

	@Column(nullable = false)
	private Integer price;

	// 명시적 제약을 통해 createdAt 누락 방지 → 데이터 무결성 보장
	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	private AuctionHistory(Auction auction, User buyer, Integer price) {
		this.auction = auction;
		this.buyer = buyer;
		this.price = price;
	}

	public static AuctionHistory of(Auction auction, User buyer, Integer price) {
		return new AuctionHistory(auction, buyer, price);
	}
}

package com.example.palayo.domain.auctionhistory.entity;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.entity.Auction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public AuctionHistory(Auction auction, User buyer, Integer price) {
		this.auction = auction;
		this.buyer = buyer;
		this.price = price;
		this.createdAt = LocalDateTime.now();
	}

	public static AuctionHistory of(Auction auction, User buyer, Integer price) {
		return new AuctionHistory(auction, buyer, price);
	}
}

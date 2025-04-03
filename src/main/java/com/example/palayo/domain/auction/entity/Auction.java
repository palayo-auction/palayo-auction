package com.example.palayo.domain.auction.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.example.palayo.domain.auction.enums.AuctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "auctions")
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	// 낙찰자: 경매 등록 시점엔 없고 낙찰 처리 시 설정됨
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id")
	private User buyer;

	@Column(name = "min_price", nullable = false)
	private Integer minPrice;

	@Column(name = "max_price", nullable = false)
	private Integer maxPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuctionStatus status;

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "expired_at", nullable = false)
	private LocalDateTime expiredAt;

	// 명시적 제약을 통해 createdAt 누락 방지 → 데이터 무결성 보장
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// 입찰 시각 기록: 생성 시점 기준으로 자동 세팅
	// buyer는 경매 생성 시점엔 정해지지 않으며 낙찰 후 별도로 설정되므로 생성자에서 제외
	public Auction(Item item, User seller, Integer minPrice, Integer maxPrice,
		AuctionStatus status, LocalDateTime startedAt, LocalDateTime expiredAt) {
		this.item = item;
		this.seller = seller;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.status = status;
		this.startedAt = startedAt;
		this.expiredAt = expiredAt;
		this.createdAt = LocalDateTime.now();
	}

	public static Auction of(Item item, User seller, Integer minPrice, Integer maxPrice,
		AuctionStatus status, LocalDateTime startedAt, LocalDateTime expiredAt) {
		return new Auction(item, seller, minPrice, maxPrice, status, startedAt, expiredAt);
	}
}

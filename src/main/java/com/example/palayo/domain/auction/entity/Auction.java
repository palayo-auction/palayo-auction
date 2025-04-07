package com.example.palayo.domain.auction.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.palayo.domain.auction.enums.AuctionStatus;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "auctions")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Auction {

	// 경매 ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 경매 대상 상품 (경매 실패, 유찰 시 재경매 허용 → 다대일 관계)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	// 낙찰자 (처음엔 null, 낙찰 시 지정)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "winning_bidder_id")
	private User winningBidder;

	// 경매 시작가
	@Column(nullable = false)
	private int startingPrice;

	// 즉시 낙찰가
	@Column(nullable = false)
	private int buyoutPrice;

	// 현재 최고 입찰가 (입찰이 없을 경우 null)
	@Column(nullable = false)
	private Integer currentPrice;

	// 입찰 단위 (최소 입찰 증가액)
	@Column(nullable = false)
	private int bidIncrement;

	// 경매 상태 (READY, ACTIVE, SUCCESS, FAILED)
	// 직접 설정이 불가능하며 내부 로직에 의해 자동으로 관리됨
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuctionStatus status;

	// 경매 시작 일시
	@Column(nullable = false)
	private LocalDateTime startedAt;

	// 경매 종료 일시
	@Column(nullable = false)
	private LocalDateTime expiredAt;

	// 생성 일시 (자동으로 설정됨)
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// 삭제 일시 (소프트 딜리트)
	private LocalDateTime deletedAt;

	private Auction(Item item, int startingPrice, int buyoutPrice, int bidIncrement, LocalDateTime startedAt,
		LocalDateTime expiredAt) {
		this.item = item;
		this.startingPrice = startingPrice;
		this.buyoutPrice = buyoutPrice;
		this.bidIncrement = bidIncrement;
		this.startedAt = startedAt;
		this.expiredAt = expiredAt;
	}

	public static Auction of(Item item, int startingPrice, int buyoutPrice, int bidIncrement, LocalDateTime startedAt,
		LocalDateTime expiredAt) {
		return new Auction(item, startingPrice, buyoutPrice, bidIncrement, startedAt, expiredAt);
	}

	// 경매 상태를 READY로 자동 설정
	public void markAsReady() {
		this.status = AuctionStatus.READY;
	}

	// 경매 상태를 ACTIVE로 자동 설정
	public void markAsActive() {
		this.status = AuctionStatus.ACTIVE;
	}

	// 현재 최고 입찰가 갱신 (입찰 시 호출)
	public void updateCurrentPrice(int newPrice) {
		this.currentPrice = newPrice;
	}

	// 경매 상태를 SUCCESS로 자동 설정
	public void markAsSuccess(User winningBidder) {
		this.status = AuctionStatus.SUCCESS;
		this.winningBidder = winningBidder;
	}

	// 경매 상태를 FAILED로 자동 설정
	public void markAsFailed() {
		this.status = AuctionStatus.FAILED;
	}

	// 경매 상태를 DELETED로 자동 설정 (소프트 딜리트)
	public void markAsDeleted() {
		this.deletedAt = LocalDateTime.now();
		this.status = AuctionStatus.DELETED;
	}
}


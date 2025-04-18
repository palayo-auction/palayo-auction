package com.example.palayo.domain.deposithistory.entity;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import com.example.palayo.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "deposit_histories")
@EntityListeners(AuditingEntityListener.class)
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
	private int deposit;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DepositStatus status;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public DepositHistory(Auction auction, User user, int deposit, DepositStatus status) {
		this.auction = auction;
		this.user = user;
		this.deposit = deposit;
		this.status = status;
	}

	public void updateStatus(DepositStatus status) {
		this.status = status;
	}
}



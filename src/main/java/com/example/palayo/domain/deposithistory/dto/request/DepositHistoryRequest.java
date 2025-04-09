package com.example.palayo.domain.deposithistory.dto;

import com.example.palayo.domain.deposithistory.enums.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositHistoryRequest {

	private Long auctionId;
	private Long userId;
	private Long deposit;
	private DepositStatus status;
}
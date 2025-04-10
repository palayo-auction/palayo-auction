package com.example.palayo.domain.auctionhistory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateBidRequest {

	@NotNull(message = "입찰 금액은 필수입니다.")
	private int bidPrice; // 입찰 금액
}

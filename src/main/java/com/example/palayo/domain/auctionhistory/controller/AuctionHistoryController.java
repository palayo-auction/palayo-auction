package com.example.palayo.domain.auctionhistory.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auctionhistory.dto.request.CreateBidRequest;
import com.example.palayo.domain.auctionhistory.dto.response.BidHistoryResponse;
import com.example.palayo.domain.auctionhistory.dto.response.BidResponse;
import com.example.palayo.domain.auctionhistory.service.AuctionHistoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuctionHistoryController {

	private final AuctionHistoryService auctionHistoryService;

	// 입찰 생성
	@PostMapping("/v1/auctions/{auctionId}/bid")
	public Response<BidResponse> createBid(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long auctionId,
		@Valid @RequestBody CreateBidRequest request
	) {
		BidResponse response = auctionHistoryService.createBid(authUser, auctionId, request);
		return Response.of(response);
	}

	// 특정 경매의 입찰 내역 조회
	@GetMapping("/v1/auctions/{auctionId}/histories")
	public Response<List<BidHistoryResponse>> getAuctionBidHistories(
		@PathVariable Long auctionId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<BidHistoryResponse> bidHistories = auctionHistoryService.getAuctionBidHistories(auctionId, page, size);
		return Response.fromPage(bidHistories);
	}

	// 내가 참여한 경매 다건 조회
	@GetMapping("/v1/auctions/participated")
	public Response<List<AuctionListResponse>> getParticipatedAuctions(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<AuctionListResponse> auctions = auctionHistoryService.getParticipatedAuctions(authUser, page, size);
		return Response.fromPage(auctions);
	}

	// 내가 참여한 경매 단건 조회
	@GetMapping("/v1/auctions/participated/{auctionId}")
	public Response<AuctionDetailResponse> getParticipatedAuctionDetail(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long auctionId
	) {
		AuctionDetailResponse response = auctionHistoryService.getParticipatedAuctionDetail(authUser, auctionId);
		return Response.of(response);
	}
}

package com.example.palayo.domain.auction.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.auction.dto.request.CreateAuctionRequest;
import com.example.palayo.domain.auction.dto.response.AuctionDetailResponse;
import com.example.palayo.domain.auction.dto.response.AuctionListResponse;
import com.example.palayo.domain.auction.dto.response.AuctionResponse;
import com.example.palayo.domain.auction.dto.response.MyAuctionDetailResponse;
import com.example.palayo.domain.auction.service.AuctionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuctionController {

	private final AuctionService auctionService;

	// 경매 생성
	@PostMapping("/v1/auctions")
	public Response<AuctionResponse> saveAuction(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateAuctionRequest request
	) {
		return Response.of(auctionService.saveAuction(authUser, request));
	}

	// 진행 중인 경매 다건 조회
	@GetMapping("/v1/auctions")
	public Response<List<AuctionListResponse>> getAuctions(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<AuctionListResponse> auctions = auctionService.getAuctions(page, size);
		return Response.fromPage(auctions);
	}

	// 진행 중인 경매 단건 조회
	@GetMapping("/v1/auctions/{auctionId}")
	public Response<AuctionDetailResponse> getAuction(@PathVariable Long auctionId) {
		AuctionDetailResponse auction = auctionService.getAuction(auctionId);
		return Response.of(auction);
	}

	// 내가 등록한 경매 다건 조회
	@GetMapping("/v1/auctions/my")
	public Response<List<AuctionListResponse>> getMyAuctions(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<AuctionListResponse> auctions = auctionService.getMyAuctions(authUser, page, size);
		return Response.fromPage(auctions);
	}

	// 내가 등록한 경매 단건 조회
	@GetMapping("/v1/auctions/my/{auctionId}")
	public Response<MyAuctionDetailResponse> getMyAuction(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long auctionId
	) {
		MyAuctionDetailResponse auction = auctionService.getMyAuction(authUser, auctionId);
		return Response.of(auction);
	}

	// 내가 등록한 경매 삭제
	@DeleteMapping("/v1/auctions/my/{auctionId}")
	public Response<Void> deleteAuction(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long auctionId
	) {
		auctionService.deleteAuction(authUser, auctionId);
		return Response.of(null);
	}
}

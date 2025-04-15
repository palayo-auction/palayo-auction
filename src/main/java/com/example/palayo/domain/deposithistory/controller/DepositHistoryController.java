package com.example.palayo.domain.deposithistory.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryRequest;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepositHistoryController {

    private final DepositHistoryService depositHistoryService;

    // 단건 조회
    @GetMapping("/v1/deposithistories/{id}")
    public Response<DepositHistoryResponse> getDepositHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser authUser) {
        DepositHistoryResponse depositHistoryResponse = depositHistoryService.getDepositHistory(id, authUser);
        return Response.of(depositHistoryResponse);
    }

    // 다건 조회 (페이징 처리)
    @GetMapping("/v1/deposithistories")
    public Response<List<DepositHistoryResponse>> getDepositHistoryList(
            @RequestParam Long auctionId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser) {
        Page<DepositHistoryResponse> depositHistoryPage = depositHistoryService.getDepositHistoryList(auctionId, status, page, size, authUser);
        return Response.fromPage(depositHistoryPage);
    }

    // 보증금 이력 생성 -> 입찰 시 자동생성 되는 건데, 이 API가 필요한지 의문
    @PostMapping("/v1/deposithistories")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Void> createDepositHistory(
            @RequestBody DepositHistoryRequest depositHistoryRequest,
            @AuthenticationPrincipal AuthUser authUser) {

        // DepositHistoryRequest에서 필요한 데이터 추출
        Long userId = authUser.getUserId();  // 현재 인증된 유저의 ID
        Long auctionId = depositHistoryRequest.getAuctionId();
        int depositAmount = depositHistoryRequest.getDepositAmount();

        // 보증금 이력 생성
        depositHistoryService.createDepositHistory(userId, auctionId, Math.toIntExact(depositAmount));

        return Response.of(null); // 생성이 완료된 후, 반환 값 없음
    }
}

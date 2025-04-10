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

    // 보증금 이력 생성
    @PostMapping("/v1/deposithistories")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<DepositHistoryResponse> createDepositHistory(
            @RequestBody DepositHistoryRequest depositHistoryRequest,
            @AuthenticationPrincipal AuthUser authUser) {
        DepositHistoryResponse depositHistoryResponse = depositHistoryService.createDepositHistory(depositHistoryRequest, authUser);
        return Response.of(depositHistoryResponse);
    }
}

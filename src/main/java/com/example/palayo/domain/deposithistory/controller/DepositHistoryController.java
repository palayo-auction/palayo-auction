package com.example.palayo.domain.deposithistory.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryRequest;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DepositHistoryController {

    private final DepositHistoryService depositHistoryService;

    // 단건 조회
    @GetMapping("/deposithistory/{id}")
    public DepositHistoryResponse getDepositHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser authUser) {
        return depositHistoryService.getDepositHistory(id, authUser);
    }

    // 다건 조회 (페이징 처리)
    @GetMapping("/deposithistory")
    public Page<DepositHistoryResponse> getDepositHistoryList(
            @RequestParam Long auctionId,
            @RequestParam(required = false) String status,
            Pageable pageable,
            @AuthenticationPrincipal AuthUser authUser) {
        return depositHistoryService.getDepositHistoryList(auctionId, status, pageable, authUser);
    }

    // 보증금 이력 생성
    @PostMapping("/deposithistory")
    @ResponseStatus(HttpStatus.CREATED)
    public DepositHistoryResponse createDepositHistory(
            @RequestBody DepositHistoryRequest depositHistoryRequest,
            @AuthenticationPrincipal AuthUser authUser) {
        return depositHistoryService.createDepositHistory(depositHistoryRequest, authUser);
    }

    // 보증금 이력 수정
    @PutMapping("/deposithistory/{id}")
    public DepositHistoryResponse updateDepositHistory(
            @PathVariable Long id,
            @RequestBody DepositHistoryRequest depositHistoryRequest,
            @AuthenticationPrincipal AuthUser authUser) {
        return depositHistoryService.updateDepositHistory(id, depositHistoryRequest, authUser);
    }

    // 보증금 이력 삭제
    @DeleteMapping("/deposithistory/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepositHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser authUser) {
        depositHistoryService.deleteDepositHistory(id, authUser);
    }
}

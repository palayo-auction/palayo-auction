package com.example.palayo.domain.deposithistory.controller;

import com.example.palayo.domain.deposithistory.dto.DepositHistoryRequest;
import com.example.palayo.domain.deposithistory.dto.DepositHistoryResponse;
import com.example.palayo.domain.deposithistory.service.DepositHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DepositHistoryController {

	private final DepositHistoryService depositHistoryService;

	// 단건 조회
	@GetMapping("/deposithistory/{id}")
	public DepositHistoryResponse getDepositHistory(@PathVariable Long id) {
		return depositHistoryService.getDepositHistory(id);
	}

	// 다건 조회 (페이징 처리)
	@GetMapping("/deposithistory")
	public Page<DepositHistoryResponse> getDepositHistoryList(
		@RequestParam Long auctionId,
		@RequestParam Long userId,
		@RequestParam(required = false) String status,
		Pageable pageable) {
		return depositHistoryService.getDepositHistoryList(auctionId, userId, status, pageable);
	}

	// 보증금 이력 생성 -> 보증금은 입찰이 발생할 때 자동으로 생성되는데, 필요한 쿼리인지 논의 필요
	@PostMapping("/deposithistory")
	@ResponseStatus(HttpStatus.CREATED)
	public DepositHistoryResponse createDepositHistory(@RequestBody DepositHistoryRequest depositHistoryRequest) {
		return depositHistoryService.createDepositHistory(depositHistoryRequest);
	}

	// 보증금 이력 수정
	@PutMapping("/deposithistory/{id}")
	public DepositHistoryResponse updateDepositHistory(
		@PathVariable Long id,
		@RequestBody DepositHistoryRequest depositHistoryRequest) {
		return depositHistoryService.updateDepositHistory(id, depositHistoryRequest);
	}

	// 보증금 이력 삭제
	@DeleteMapping("/deposithistory/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDepositHistory(@PathVariable Long id) {
		depositHistoryService.deleteDepositHistory(id);
	}
}
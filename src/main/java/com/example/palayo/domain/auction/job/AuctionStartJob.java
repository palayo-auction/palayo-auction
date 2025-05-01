package com.example.palayo.domain.auction.job;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.palayo.domain.auction.entity.Auction;
import com.example.palayo.domain.auction.service.AuctionService;
import com.example.palayo.domain.auction.repository.AuctionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionStartJob implements Job {

    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    @Override
    public void execute(JobExecutionContext context) {
        Long auctionId = (Long) context.getJobDetail().getJobDataMap().get("auctionId");

        if (auctionId == null || auctionId == 0L) {
            log.warn("[AuctionStartJob] 유효하지 않은 auctionId: {}", auctionId);
            throw new BaseException(ErrorCode.SCHEDULED_JOB_INVALID_INPUT, "경매 ID가 null 또는 0입니다.");
        }

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BaseException(ErrorCode.INVALID_AUCTION_ID, "해당 경매를 찾을 수 없습니다."));

        try {
            auctionService.markAuctionAsActive(auctionId);
            log.info("[AuctionStartJob] 경매 시작 처리 완료: auctionId = {}", auctionId);
        } catch (Exception e) {
            log.info("[AuctionStartJob] 경매 시작 처리 중 예외 발생: auctionId = {}", auctionId, e);
            throw new BaseException(ErrorCode.AUCTION_START_FAILED, "경매 시작 처리 실패");
        }
    }
}
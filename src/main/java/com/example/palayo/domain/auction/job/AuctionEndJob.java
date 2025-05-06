//package com.example.palayo.domain.auction.job;
//
//import com.example.palayo.common.exception.BaseException;
//import com.example.palayo.common.exception.ErrorCode;
//import com.example.palayo.domain.auction.service.AuctionService;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class AuctionEndJob implements Job {
//
//    @Autowired
//    private AuctionService auctionService;
//
//    @Override
//    public void execute(JobExecutionContext context) {
//        Long auctionId = context.getMergedJobDataMap().getLong("auctionId");
//
//        if (auctionId == null || auctionId == 0L) {
//            log.info("[AuctionEndJob] 경매 ID가 null이거나 0입니다.");
//            // Quartz 스케줄러 실행 중 경매 ID가 없는 경우 예외 발생
//            throw new BaseException(ErrorCode.INVALID_AUCTION_ID, "경매 ID가 null 또는 0입니다.");
//        }
//
//        log.info("[Quartz] AuctionEndJob 실행: auctionId = {}", auctionId);
//
//        try {
//            auctionService.finishAuction(auctionId);
//            log.info("[AuctionEndJob] 경매 종료 완료: auctionId = {}", auctionId);
//        } catch (Exception e) {
//            log.info("[AuctionEndJob] 경매 종료 중 예외 발생: auctionId = {}", auctionId, e);
//            throw new BaseException(ErrorCode.AUCTION_FINISH_FAILED, "경매 종료 처리 중 문제가 발생했습니다.");
//        }
//    }
//}

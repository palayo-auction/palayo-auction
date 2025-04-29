package com.example.palayo.domain.auction.job;

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
public class AuctionStartJob implements Job {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 트리거된 경매 ID 가져오기
        Long auctionId = (Long) context.getJobDetail().getJobDataMap().get("auctionId");

        // auctionId가 null인지 확인하고, 로그로 확인하기
        if (auctionId == null) {
            // null이면 경고 로그 출력
            log.warn("[ERROR] 경매 ID가 null입니다. Quartz Job 실행 실패!");
        } else {
            // null이 아니면 정상 경매 ID 출력
            log.warn("[INFO] 트리거된 경매 ID: " + auctionId);
        }

        // auctionId 값이 null이 아닌 경우에만 이후 작업 진행
        if (auctionId != null) {
            // 여기서 auctionId를 사용하여 경매 상태를 ACTIVE로 업데이트
            // 예: auctionService.markAuctionAsActive(auctionId);
        }

        // 테스트용 로그 (필요 시 주석 해제)
        System.out.println("[Quartz] AuctionStartJob 실행됨: auctionId = " + auctionId);

        // 경매 찾기
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new JobExecutionException("Auction not found"));

        // 경매 상태를 ACTIVE로 업데이트
        auctionService.markAuctionAsActive(auctionId);  // 기존의 상태 업데이트 코드 대신 서비스 호출


    }
}
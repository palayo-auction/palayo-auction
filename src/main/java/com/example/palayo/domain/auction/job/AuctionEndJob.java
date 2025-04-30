package com.example.palayo.domain.auction.job;

import com.example.palayo.domain.auction.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuctionEndJob implements Job {

    @Autowired
    private AuctionService auctionService;

    @Override
    public void execute(JobExecutionContext context) {
        Long auctionId = context.getMergedJobDataMap().getLong("auctionId");
        log.warn("[Quartz] AuctionEndJob 실행됨: auctionId = {}", auctionId);

        try {
            auctionService.finishAuction(auctionId);
            log.warn("[AuctionEndJob] finishAuction 완료: auctionId = {}", auctionId);
        } catch (Exception e) {
            log.warn("[AuctionEndJob] finishAuction 예외 발생: auctionId = {}", auctionId, e);
        }
    }
}

package com.example.palayo.domain.auction.job;

import com.example.palayo.domain.auction.service.AuctionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuctionEndJob implements Job {

    @Autowired
    private AuctionService auctionService;

    @Override
    public void execute(JobExecutionContext context) {
        Long auctionId = context.getMergedJobDataMap().getLong("auctionId");

//         테스트용 로그 (필요 시 주석 해제)
         System.out.println("[Quartz] AuctionEndJob 실행됨: auctionId = " + auctionId);

        auctionService.finishAuction(auctionId);
    }
}

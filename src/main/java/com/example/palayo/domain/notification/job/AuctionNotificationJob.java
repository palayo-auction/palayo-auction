package com.example.palayo.domain.notification.job;

import com.example.palayo.domain.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AuctionNotificationJob implements Job {


    private ApplicationContext context;

    @Override
    public void execute(JobExecutionContext jobContext) {
        try {
            Map<String, Object> dataMap = jobContext.getMergedJobDataMap().getWrappedMap();

            Long userId = Long.valueOf(dataMap.get("userId").toString());
            String title = dataMap.get("title").toString();
            String body = dataMap.get("body").toString();

            NotificationService notificationService = context.getBean(NotificationService.class);
            notificationService.sendAuctionNotification(userId, title, body);

            log.info("[Quartz Job] 알림 전송 완료 - userId: {}, title: {}", userId, title);
        } catch (Exception e) {
            log.error("Quartz Job 실행 중 오류", e);
        }
    }
}

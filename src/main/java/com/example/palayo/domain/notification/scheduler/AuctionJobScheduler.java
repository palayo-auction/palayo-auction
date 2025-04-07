package com.example.palayo.domain.notification.scheduler;

import com.example.palayo.domain.notification.job.AuctionNotificationJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuctionJobScheduler {

    private final Scheduler scheduler;

    public void scheduleNotificationJob(Long userId, String title, String body, ZonedDateTime runAt) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(AuctionNotificationJob.class)
                    .withIdentity(UUID.randomUUID().toString(), "auction-notification")
                    .usingJobData("userId", userId)
                    .usingJobData("title", title)
                    .usingJobData("body", body)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .startAt(Date.from(runAt.toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("알림 Job 스케줄링 실패", e);
        }
    }
}

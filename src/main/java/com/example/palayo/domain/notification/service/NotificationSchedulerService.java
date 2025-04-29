package com.example.palayo.domain.notification.service;

import com.example.palayo.domain.notification.quartz.ScheduledNotificationJob;
import com.example.palayo.domain.notification.redis.RedisNotification;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationSchedulerService {

    private final Scheduler scheduler;

//    public void scheduleNotification(RedisNotification noti) throws SchedulerException {
//        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("userId", noti.getUserId());
//        jobDataMap.put("title", noti.getTitle());
//        jobDataMap.put("body", noti.getBody());
//        jobDataMap.put("auctionId", noti.getData().get("auctionId"));
//
//        JobDetail jobDetail = JobBuilder.newJob(ScheduledNotificationJob.class)
//                .withIdentity("noti-" + UUID.randomUUID(), "notifications")
//                .usingJobData(jobDataMap)
//                .storeDurably()
//                .build();
//
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .forJob(jobDetail)
//                .startAt(Timestamp.valueOf(noti.getScheduledAt()))
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
//                .build();
//
//        scheduler.scheduleJob(jobDetail, trigger);
//    }

    public void scheduleNotification(RedisNotification noti) throws SchedulerException {
        // JobDataMap에 notificationKey를 전달
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("notificationKey", "notification:" + noti.getUserId() + ":" + noti.getData().get("auctionId") + ":" + noti.getType());

        // JobDetail 생성 및 JobDataMap 설정
        JobDetail jobDetail = JobBuilder.newJob(ScheduledNotificationJob.class)
                .withIdentity("noti-job-" + UUID.randomUUID(), "notifications")  // Job 고유 식별자 설정
                .usingJobData(jobDataMap)  // Job에 데이터를 전달
                .storeDurably()  // Job을 지속적으로 저장
                .build();

        // LocalDateTime을 Date로 변환
        Date triggerStartDate = Date.from(noti.getScheduledAt().atZone(ZoneId.systemDefault()).toInstant());

        // Trigger 생성 (알림 예약 시간)
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .startAt(triggerStartDate)  // 알림 예약 시간을 설정
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        // Job과 Trigger를 스케줄러에 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
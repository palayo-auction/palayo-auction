package com.example.palayo.domain.notification.quartz;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuartzInitializer {

    private final Scheduler scheduler;

    @PostConstruct
    public void clearAllQuartzJobs() throws SchedulerException {
        scheduler.clear(); // 등록된 모든 Job과 Trigger 제거
        System.out.println("✅ Quartz 스케줄러 초기화 완료 (local 프로필)");
    }
}
package com.example.palayo.domain.notification.quartz;
import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CustomJobFactory extends SpringBeanJobFactory {

    @Autowired
    private ApplicationContext applicationContext; // Spring ApplicationContext를 사용하여 의존성 주입

    @Override
    public Object createJobInstance(TriggerFiredBundle bundle) {
        try {
            // Job 생성 및 의존성 주입
            Object job = super.createJobInstance(bundle);
            applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
            return job;
        } catch (Exception e) {
            // 예외 처리 로직
            throw new BaseException(ErrorCode.CREATE_JOB_FAILED, null);
        }
    }
}

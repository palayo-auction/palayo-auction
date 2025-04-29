package com.example.palayo.config;

import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfig {

	@Bean
	public JobFactory jobFactory() {
		return new SpringBeanJobFactory();
	}

	// SchedulerFactoryBean 등록
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setJobFactory(jobFactory);
		return factory;
	}
	@Bean
	public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws Exception {
		return factoryBean.getScheduler();
	}

}
package com.example.palayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableMongoAuditing
public class PalayoApplication {

	public static void main(String[] args) {
		System.out.println("나 살아있어요");
		try {
			SpringApplication.run(PalayoApplication.class, args);
		} catch (Exception e) {
			System.out.println("예외 발생함:");
			e.printStackTrace();
		}
		SpringApplication.run(PalayoApplication.class, args);
	}
}

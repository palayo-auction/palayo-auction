package com.example.palayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableMongoAuditing
public class PalayoApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(PalayoApplication.class);
		System.out.println(" PalayoApplication started ");
		app.setWebApplicationType(WebApplicationType.SERVLET);
		app.run(args);
		System.out.println(" PalayoApplication after started ");



	}

}

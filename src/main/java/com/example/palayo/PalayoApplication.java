package com.example.palayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PalayoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PalayoApplication.class, args);
    }

}

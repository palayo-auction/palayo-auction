package com.example.palayo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 🔥 API 경로 설정
                        .allowedOrigins("http://localhost:63342") // 🔥 프론트엔드 도메인 허용
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 🔥 허용할 HTTP 메서드
                        .allowCredentials(true);
            }
        };
    }
}

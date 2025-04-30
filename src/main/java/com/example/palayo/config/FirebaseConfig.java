package com.example.palayo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


@Configuration
@Slf4j
public class FirebaseConfig {

    public FirebaseConfig() {
        try {
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("service_account_key.json"); // ✅ 경로 수정

            if (serviceAccount == null) {
                throw new FileNotFoundException("❌ Firebase key 파일을 찾을 수 없습니다.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ FirebaseApp 초기화 완료");
            }
        } catch (Exception e) {
            log.error("❌ Firebase 초기화 실패", e);
        }
    }
}
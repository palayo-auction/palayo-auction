package com.example.palayo.common.firebase;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // service-account.json 파일을 resources 폴더에서 로드
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("palayo-auction-test-firebase-adminsdk-fbsvc-d7b4c29e3d.json");

            if (serviceAccount == null) {
                throw new BaseException(ErrorCode.SERVICEACCOUNT_NOT_FOUND,null);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Firebase 중복 초기화 방지
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("Firebase가 성공적으로 초기화되었습니다!");

        } catch (IOException e) {
            throw new BaseException(ErrorCode.FIREBASE_INIT_FAIL,null);
        }
    }
}

package com.example.palayo.domain.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    public void sendPushNotification(String token, String title, String body) {
        try {
            // ✅ FCM 메시지 생성
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            // ✅ FCM 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM 메시지 전송 완료: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static final String path = "service-account.json";
//
//    @PostConstruct
//    public void init(){
//        try{
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(path).getInputStream())).build();
//
//            if(FirebaseApp.getApps().isEmpty()){
//                FirebaseApp.initializeApp(options);
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    // 🔹 1️⃣ UID 기반으로 푸시 알림 보내기
//    public String sendNotification(String uid, String title, String body) {
//        try {
//            // 🔹 UID -> FCM 토큰 가져오기
//            String token = FirebaseAuth.getInstance().getUser(uid).getCustomClaims().get("fcmToken").toString();
//
//            // 🔹 푸시 메시지 생성
//            Notification notification = Notification.builder()
//                    .setTitle(title)
//                    .setBody(body)
//                    .build();
//
//            Message message = Message.builder()
//                    .setToken(token)
//                    .setNotification(notification)
//                    .build();
//
//            // 🔹 푸시 메시지 전송
//            String response = FirebaseMessaging.getInstance().send(message);
//            return "푸시 알림 전송 완료: " + response;
//        } catch (FirebaseAuthException | FirebaseMessagingException e) {
//            return "푸시 알림 전송 실패: " + e.getMessage();
//        }
//    }
//
//    public String createCustomToken(String uid) throws FirebaseAuthException {
//        return FirebaseAuth.getInstance().createCustomToken(uid);
//    }
//
//    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
//        return FirebaseAuth.getInstance().verifyIdToken(idToken);
//    }

}

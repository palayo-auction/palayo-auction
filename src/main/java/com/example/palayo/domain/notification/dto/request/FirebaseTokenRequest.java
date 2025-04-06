package com.example.palayo.domain.notification.dto.request;

import lombok.Data;

@Data
public class FirebaseTokenRequest {
        private String token;
        private String title;
        private String body;
}

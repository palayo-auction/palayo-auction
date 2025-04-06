package com.example.palayo.domain.notification.dto.request;

import lombok.Data;

@Data
public class NotificationRequest {
        //token은 사용자 db에 저장하여 각각 불러오는 방식으로 진행? -> 팀원들과 논의후 결정
        private String token;
        private String title;
        private String body;
}

package com.example.palayo.domain.notification.entity;

import com.example.palayo.common.util.DataMapConverter;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String body;

    @Convert(converter = DataMapConverter.class)
    private Map<String, String> data;
    //한명의 user가 여러 브라우지 여러 디바이스에서 접속시 token이 여러개 생길수 있음
    //알림을 허용하지 않은 사람이 있을수 있으니 나누는게 좋을것같음
    //팀원과 상의후 확정 예정
    private String token;

}

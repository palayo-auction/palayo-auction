package com.example.palayo.domain.user.entity;

import com.example.palayo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//TODO User은 H2에서 예약어입니다. 어떤 예약어든 피해주세요 UserInfo 혹은 MemberInfo 추천드려요
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(columnDefinition = "int default 0")
    private int pointAmount;

    private LocalDateTime deletedAt;

    private User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public static User of(String email, String password, String nickname) {
        return new User(email, password, nickname);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @PreUpdate
    public void deleteUser() {
        this.deletedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}

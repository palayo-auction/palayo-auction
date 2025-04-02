package com.example.palayo.domain.user.entity;

import com.example.palayo.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false)
    private int pointAmount;

    private User(String email, String nickname, int pointAmount) {
        this.email = email;
        this.nickname = nickname;
        this.pointAmount = pointAmount;
    }

    public static User of(String email, String nickname, int pointAmount) {
        return new User(email, nickname, pointAmount);
    }
}

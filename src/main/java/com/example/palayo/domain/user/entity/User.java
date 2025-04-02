package com.example.palayo.domain.user.entity;

import com.example.palayo.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false)
    private int pointAmount;

    private User(String email, String password, String nickname, int pointAmount) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.pointAmount = pointAmount;
    }

    public static User of(String email, String password, String nickname, int pointAmount) {
        return new User(email, password, nickname, pointAmount);
    }
}

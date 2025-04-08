package com.example.palayo.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    private Long id;

    private String email;

    private String nickname;

    private int pointAmount;

    public static UserResponse of(Long id, String email, String nickname, int pointAmount) {
        return new UserResponse(id, email, nickname, pointAmount);
    }
}

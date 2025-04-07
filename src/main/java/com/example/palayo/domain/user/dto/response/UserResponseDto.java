package com.example.palayo.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
    private Long id;

    private String email;

    private String nickname;

    private int pointAmount;

    public static UserResponseDto of(Long id, String email, String nickname, int pointAmount) {
        return new UserResponseDto(id, email, nickname, pointAmount);
    }
}

package com.example.palayo.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUserResponseDto {
    private Long id;

    private String email;

    private String nickname;

    private int pointAmount;

    private String token;

    public static LoginUserResponseDto of(Long id, String email, String nickname, int pointAmount, String token) {
        return new LoginUserResponseDto(id, email, nickname, pointAmount, token);
    }
}

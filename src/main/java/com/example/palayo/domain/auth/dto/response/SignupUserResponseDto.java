package com.example.palayo.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupUserResponseDto {
    private Long id;

    private String email;

    private String nickname;

    private int pointAmount;

    private String bearerToken;

    public static SignupUserResponseDto of(Long id, String email, String nickname, int pointAmount, String bearerToken) {
        return new SignupUserResponseDto(id, email, nickname, pointAmount, bearerToken);
    }
}

package com.example.palayo.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUserResponseDto {

    private String token;

    public static LoginUserResponseDto of(String token) {
        return new LoginUserResponseDto(token);
    }
}

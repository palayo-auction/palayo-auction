package com.example.palayo.domain.notification.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {

    private String token;

    public static TokenResponse of(String token) {
        return new TokenResponse(token);
    }
}

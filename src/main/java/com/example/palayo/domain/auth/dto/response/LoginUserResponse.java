package com.example.palayo.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUserResponse {

    private String token;

    public static LoginUserResponse of(String token) {
        return new LoginUserResponse(token);
    }
}

package com.example.palayo.domain.auth.dto.response;

import com.example.palayo.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSaveResponseDto {
    private Long id;

    private String email;

    private String nickname;

    public static AuthSaveResponseDto of(User user) {
        return new AuthSaveResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }
}

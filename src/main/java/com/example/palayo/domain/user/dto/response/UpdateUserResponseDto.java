package com.example.palayo.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserResponseDto {
    private Long id;

    private String email;

    private String nickname;

    private int pointAmount;

    public static UpdateUserResponseDto of(Long id, String email, String nickname, int pointAmount) {
        return new UpdateUserResponseDto(id, email, nickname, pointAmount);
    }
}

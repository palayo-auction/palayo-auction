package com.example.palayo.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequestDto {

    private String nickname;

    private String rawPassword;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
            message = "비밀번호는 하나 이상의 영문 소문자와 하나 이상의 영문 대문자, 하나 이상의 특수문자(@#$%^&+=)를 포함하셔서 최소 8자 이상이어야합니다.")
    private String newPassword;
}

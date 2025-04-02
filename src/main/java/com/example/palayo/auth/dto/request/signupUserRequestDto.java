package com.example.palayo.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class signupUserRequestDto {

    @Email(message = "이메일 형식에 어긋납니다.")
    @NotBlank
    private String email;

    // 최소 8자리, 하나 이상의 대문자, 소문자, 숫자, 특수문자
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "최소 8자리, 하나 이상의 대문자, 소문자, 숫자, 특수문자를 포함하셔야 합니다.")
    private String password;

    private String nickname;
}

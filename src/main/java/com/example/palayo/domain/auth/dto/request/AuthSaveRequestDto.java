package com.example.palayo.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSaveRequestDto {

    @Email
    @NotBlank
    private String email;

    // 최소 8자리, 하나 이상의 대문자, 소문자, 숫자, 특수문자
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$")
    private String password;

    private String nickname;
}

package com.example.palayo.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupUserRequest {

    @Email(message = "이메일 형식에 어긋납니다.")
    @NotBlank
    private String email;

    // 최소 8자리, 하나 이상의 대문자, 소문자, 숫자, 특수문자
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
            message = "\"비밀번호는 하나 이상의 영문 소문자와 하나 이상의 영문 대문자, 하나 이상의 특수문자(@#$%^&+=)를 포함하셔서 최소 8자 이상이어야합니다.\"")
    private String password;

    private String nickname;
}

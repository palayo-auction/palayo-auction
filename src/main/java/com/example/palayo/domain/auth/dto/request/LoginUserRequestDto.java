package com.example.palayo.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

//TODO Class 명은 카멜케이스입니다
@Getter
@AllArgsConstructor
public class LoginUserRequestDto {
    private Long id;

    @NotBlank
    @Email(message = "이메일 형식에 어긋납니다")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "비밀번호가 다릅니다")
    private String password;
}

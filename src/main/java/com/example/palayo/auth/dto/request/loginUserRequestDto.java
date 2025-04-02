package com.example.palayo.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class loginUserRequestDto {
    private Long id;
    @NotBlank
    @Email(message = "이메일 형식이 아닙니다")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "비밀번호가 다르거나 아이디가 다릅니다.")
    private String password;
}

package com.example.palayo.domain.auth.controller;

import com.example.palayo.domain.auth.dto.request.LoginUserRequestDto;
import com.example.palayo.domain.auth.dto.response.LoginUserResponseDto;
import com.example.palayo.domain.auth.dto.response.SignupUserResponseDto;
import com.example.palayo.domain.auth.service.AuthService;
import com.example.palayo.domain.auth.dto.request.SignUpUserRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO Auth 도메인 따로 나와있던 이유가 있으면 다시 옮기셔도 됩니다
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<SignupUserResponseDto> signup(@Valid @RequestBody SignUpUserRequestDto requestDto) {
		SignupUserResponseDto responseDto = authService.signUp(
				requestDto.getEmail(),
				requestDto.getPassword(),
				requestDto.getNickname()
		);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponseDto> login(@Valid @RequestBody LoginUserRequestDto requestDto) {
		LoginUserResponseDto responseDto = authService.login(
				requestDto.getEmail(),
				requestDto.getPassword()
		);
		return ResponseEntity.ok(responseDto);
	}
}

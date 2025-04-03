package com.example.palayo.auth.controller;

import com.example.palayo.auth.dto.request.loginUserRequestDto;
import com.example.palayo.auth.dto.response.LoginUserResponseDto;
import com.example.palayo.auth.dto.response.SignupUserResponseDto;
import com.example.palayo.auth.service.AuthService;
import com.example.palayo.config.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.palayo.auth.dto.request.signupUserRequestDto;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth/signup")
	public ResponseEntity<SignupUserResponseDto> signup(@Valid @RequestBody signupUserRequestDto requestDto) {
		SignupUserResponseDto responseDto = authService.authSave(
				requestDto.getEmail(),
				requestDto.getPassword(),
				requestDto.getNickname()
		);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/v1/auth/login")
	public ResponseEntity<LoginUserResponseDto> login(@Valid @RequestBody loginUserRequestDto requestDto) {
		LoginUserResponseDto responseDto = authService.login(
				requestDto.getEmail(),
				requestDto.getPassword()
		);
		return ResponseEntity.ok(responseDto);
	}
}

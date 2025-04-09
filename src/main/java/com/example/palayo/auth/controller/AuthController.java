package com.example.palayo.auth.controller;

import com.example.palayo.auth.dto.request.loginUserRequest;
import com.example.palayo.auth.dto.response.LoginUserResponse;
import com.example.palayo.auth.dto.response.SignupUserResponse;
import com.example.palayo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.palayo.auth.dto.request.signupUserRequest;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth/signup")
	public ResponseEntity<SignupUserResponse> signup(@Valid @RequestBody signupUserRequest requestDto) {
		SignupUserResponse responseDto = authService.singup(
				requestDto.getEmail(),
				requestDto.getPassword(),
				requestDto.getNickname()
		);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/v1/auth/login")
	public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody loginUserRequest requestDto) {
		LoginUserResponse responseDto = authService.login(
				requestDto.getEmail(),
				requestDto.getPassword()
		);
		return ResponseEntity.ok(responseDto);
	}
}

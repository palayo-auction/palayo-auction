package com.example.palayo.domain.auth.controller;

import com.example.palayo.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.palayo.domain.auth.dto.request.AuthSaveRequestDto;
import com.example.palayo.domain.auth.dto.response.AuthSaveResponseDto;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth")
	public ResponseEntity<AuthSaveResponseDto> authSave(@Valid @RequestBody AuthSaveRequestDto requestDto) {

		AuthSaveResponseDto authSave = authService.authSave(
				requestDto.getEmail(),
				requestDto.getPassword(),
				requestDto.getNickname(),
				0
		);

		return ResponseEntity.ok(authSave);
	}
}

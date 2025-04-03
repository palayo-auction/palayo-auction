package com.example.palayo.domain.user.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.domain.user.dto.request.UpdateUserRequestDto;
import com.example.palayo.domain.user.dto.response.UpdateUserResponseDto;
import com.example.palayo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/v1/users/nickname")
    public ResponseEntity<UpdateUserResponseDto> updateNickname(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
            ) {

        UpdateUserResponseDto updatedNickname = userService.updateNickname(
                requestDto.getNickname(),
                authUser.getUserId()
        );

        return ResponseEntity.ok(updatedNickname);
    }

    @PutMapping("v1/users/password")
    public ResponseEntity<UpdateUserResponseDto> updatePassword(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UpdateUserResponseDto updatedPassword = userService.updatePassword(
                requestDto.getRawPassword(),
                requestDto.getNewPassword(),
                authUser.getUserId()
        );

        return ResponseEntity.ok(updatedPassword);
    }
}

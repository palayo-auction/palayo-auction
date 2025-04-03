package com.example.palayo.domain.user.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.domain.user.dto.request.UpdateUserRequestDto;
import com.example.palayo.domain.user.dto.response.UserResponseDto;
import com.example.palayo.domain.user.dto.response.UserSoldResponseDto;
import com.example.palayo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/v1/users/nickname")
    public ResponseEntity<UserResponseDto> updateNickname(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
            ) {

        UserResponseDto updatedNickname = userService.updateNickname(
                requestDto.getNickname(),
                authUser.getUserId()
        );

        return ResponseEntity.ok(updatedNickname);
    }

    @PutMapping("v1/users/password")
    public ResponseEntity<UserResponseDto> updatePassword(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponseDto updatedPassword = userService.updatePassword(
                requestDto.getRawPassword(),
                requestDto.getNewPassword(),
                authUser.getUserId()
        );

        return ResponseEntity.ok(updatedPassword);
    }

    @GetMapping("v1/users/{id}/mypage")
    public ResponseEntity<UserResponseDto> mypage(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(userService.mypage(id, authUser.getUserId()));
    }

    @GetMapping("v1/users/{id}/sold")
    public ResponseEntity<UserSoldResponseDto> sold(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(userService.sold(id, authUser.getUserId()));
    }
}

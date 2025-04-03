package com.example.palayo.domain.user.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.DefaultResponse;
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
    public DefaultResponse<UserResponseDto> updateNickname(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
            ) {

        UserResponseDto updatedNickname = userService.updateNickname(
                requestDto.getNickname(),
                authUser.getUserId()
        );

        return new DefaultResponse<>(updatedNickname);
    }

    @PutMapping("v1/users/password")
    public DefaultResponse<UserResponseDto> updatePassword(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponseDto updatedPassword = userService.updatePassword(
                requestDto.getRawPassword(),
                requestDto.getNewPassword(),
                authUser.getUserId()
        );

        return new DefaultResponse<>(updatedPassword);
    }

    @GetMapping("v1/users/mypage")
    public DefaultResponse<UserResponseDto> mypage(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return new DefaultResponse<>(userService.mypage(authUser.getUserId()));
    }

    @GetMapping("v1/users/sold")
    public DefaultResponse<UserSoldResponseDto> sold(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new DefaultResponse<>(userService.sold(authUser.getUserId(), page, size));
    }
}

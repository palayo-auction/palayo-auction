package com.example.palayo.domain.user.controller;

import java.util.List;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.user.dto.request.DeleteUserRequestDto;
import com.example.palayo.domain.user.dto.request.UpdateUserRequestDto;
import com.example.palayo.domain.user.dto.response.UserResponseDto;
import com.example.palayo.domain.user.dto.response.UserItemResponseDto;
import com.example.palayo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/v1/users/nickname")
    public Response<UserResponseDto> updateNickname(
            @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponseDto updatedNickname = userService.updateNickname(
                requestDto.getNickname(),
                authUser.getUserId()
        );

        return Response.of(updatedNickname);
    }

    @PutMapping("v1/users/password")
    public Response<UserResponseDto> updatePassword(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponseDto updatedPassword = userService.updatePassword(
                requestDto.getRawPassword(),
                requestDto.getNewPassword(),
                authUser.getUserId()
        );

        return Response.of(updatedPassword);
    }

    @GetMapping("v1/users/mypage")
    public Response<UserResponseDto> mypage(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return Response.of(userService.mypage(authUser.getUserId()));
    }

    // @GetMapping("v1/users/sold")
    // public Response<List<UserItemResponseDto>> sold(
    //         @AuthenticationPrincipal AuthUser authUser,
    //         @RequestParam(defaultValue = "1") int page,
    //         @RequestParam(defaultValue = "10") int size
    // ) {
    //     return Response.fromPage(userService.sold(authUser.getUserId(), page, size));
    // }

    @DeleteMapping("v1/users")
    public Response<Void> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody DeleteUserRequestDto requestDto
    ) {
        userService.delete(authUser.getUserId(), requestDto.getPassword());
        return Response.empty();
    }
}

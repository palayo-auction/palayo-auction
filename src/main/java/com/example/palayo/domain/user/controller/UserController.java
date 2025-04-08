package com.example.palayo.domain.user.controller;

import java.util.List;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.user.dto.request.DeleteUserRequest;
import com.example.palayo.domain.user.dto.request.UpdateUserRequest;
import com.example.palayo.domain.user.dto.response.UserResponse;
import com.example.palayo.domain.user.dto.response.UserItemResponse;
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
    public Response<UserResponse> updateNickname(
            @RequestBody UpdateUserRequest requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponse updatedNickname = userService.updateNickname(
                requestDto.getNickname(),
                authUser.getUserId()
        );

        return Response.of(updatedNickname);
    }

    @PutMapping("v1/users/password")
    public Response<UserResponse> updatePassword(
            @Valid @RequestBody UpdateUserRequest requestDto,
            @AuthenticationPrincipal AuthUser authUser
    ) {

        UserResponse updatedPassword = userService.updatePassword(
                requestDto.getRawPassword(),
                requestDto.getNewPassword(),
                authUser.getUserId()
        );

        return Response.of(updatedPassword);
    }

    @GetMapping("v1/users/mypage")
    public Response<UserResponse> mypage(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return Response.of(userService.mypage(authUser.getUserId()));
    }

    // @GetMapping("v1/users/sold")
    // public Response<List<UserItemResponse>> sold(
    //         @AuthenticationPrincipal AuthUser authUser,
    //         @RequestParam(defaultValue = "1") int page,
    //         @RequestParam(defaultValue = "10") int size
    // ) {
    //     return Response.fromPage(userService.sold(authUser.getUserId(), page, size));
    // }

    @DeleteMapping("v1/users")
    public Response<Void> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody DeleteUserRequest requestDto
    ) {
        userService.delete(authUser.getUserId(), requestDto.getPassword());
        return Response.empty();
    }
}

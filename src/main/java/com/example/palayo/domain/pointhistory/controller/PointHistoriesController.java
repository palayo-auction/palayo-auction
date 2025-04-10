package com.example.palayo.domain.pointhistory.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.pointhistory.dto.PointHistoriesResponse;
import com.example.palayo.domain.pointhistory.service.PointHistoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointHistoriesController {

    private final PointHistoriesService pointHistoriesService;

    @GetMapping("/v1/pointHistories")
    public Response<List<PointHistoriesResponse>> findByUserId(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam (defaultValue = "1") int page,
            @RequestParam (defaultValue = "10") int size
    ) {
        Page<PointHistoriesResponse> byUserId = pointHistoriesService.findByUserId(authUser.getUserId(), page, size);
        return Response.fromPage(byUserId);
    }
}

package com.example.palayo.domain.pointhistory.mongo.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.pointhistory.mongo.dto.PointHistoryResponse;
import com.example.palayo.domain.pointhistory.mongo.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;

    @GetMapping("/v2/pointHistories")
    public Response<List<PointHistoryResponse>> findByUserId(
            @RequestParam (defaultValue = "1") int page,
            @RequestParam (defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return Response.fromPage(pointHistoryService.getPointHistory(authUser.getUserId(), page, size));
    }
}

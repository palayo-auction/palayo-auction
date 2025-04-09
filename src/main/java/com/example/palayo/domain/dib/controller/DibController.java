package com.example.palayo.domain.dib.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.dib.dto.response.DibListResponse;
import com.example.palayo.domain.dib.dto.response.DibResponse;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.service.DibService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class DibController {

    private final DibService dibService;

    @PostMapping("v1/dib")
    public Response<DibResponse> dibAuction(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long auctionId){
        DibResponse dibResponse = dibService.dibAuction(authUser,auctionId);
        return Response.of(dibResponse);
    }

    @GetMapping("v1/dib")
    public Response<DibResponse> getMyDib(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long dibId
    ) {
        DibResponse dib = dibService.getMyDib(authUser, dibId);
        return Response.of(dib);
    }

    @GetMapping("v1/dibs")
    public Response<List<DibListResponse>> getMyDibs(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DibListResponse> dibs = dibService.getMyDibs(authUser, page, size);
        return Response.fromPage(dibs);
    }


}

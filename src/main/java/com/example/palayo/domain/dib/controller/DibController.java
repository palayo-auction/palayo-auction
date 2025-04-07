package com.example.palayo.domain.dib.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.dib.entity.Dib;
import com.example.palayo.domain.dib.service.DibService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class DibController {

    private final DibService dibService;

    @PostMapping("v1/dib")
    public Response<Void> dibAuction(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long auctionId){
        dibService.dibAuction(authUser,auctionId);
        return Response.empty();
    }

    @DeleteMapping("v1/dib")
    public Response<Void> unDibAuction(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long auctionId){
        dibService.unDibAuction(authUser,auctionId);
        return  Response.empty();
    }

    @GetMapping("v1/dibs")
    public Response<Page<Dib>> getMyDibs(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return dibService.getMyDibs(authUser, page, size);
    }
}

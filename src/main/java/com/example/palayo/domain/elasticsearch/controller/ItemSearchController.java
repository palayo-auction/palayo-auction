package com.example.palayo.domain.elasticsearch.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.elasticsearch.dto.response.ItemSearchResponse;
import com.example.palayo.domain.elasticsearch.service.ItemSearchService;
import com.example.palayo.domain.item.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ItemSearchController {

    private final ItemSearchService itemSearchService;

    @GetMapping("/v1/items/search")
    public Response<List<ItemSearchResponse>> searchItems(
            @RequestParam (required = false) String keyword,
            @RequestParam (required = false) Category category,
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable
            )  {
        Page<ItemSearchResponse> responses = itemSearchService.searchItems(keyword, category, authUser.getUserId(), pageable);
        return Response.fromPage(responses);
    }

    @GetMapping("/v2/items/search")
    public Response<List<ItemSearchResponse>> findBySellerId(
            @AuthenticationPrincipal AuthUser authUser,
            Pageable pageable
    ) {
        Page<ItemSearchResponse> responses = itemSearchService.findBySellerId(authUser.getUserId(), pageable);
        return Response.fromPage(responses);
    }
}

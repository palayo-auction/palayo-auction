package com.example.palayo.domain.item.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.item.dto.request.ItemDeleteRequest;
import com.example.palayo.domain.item.dto.request.ItemSaveRequest;
import com.example.palayo.domain.item.dto.request.ItemUpdateRequest;
import com.example.palayo.domain.item.dto.response.ItemPageResponse;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    Response<ItemResponse> saveItem(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemSaveRequest request
    ){
        ItemResponse itemResponse = itemService.saveItem(authUser.getUserId(), request);
        return Response.of(itemResponse);
    }

    @PatchMapping("/v1/items/{itemId}")
    Response<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemUpdateRequest request
            ){
        ItemResponse itemResponse = itemService.updateItem(itemId, request, authUser.getUserId());
        return Response.of(itemResponse);
    }

    @DeleteMapping("/{itemId}")
    Response<Void> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ItemDeleteRequest request
    ){
        itemService.deleteItem(itemId, request.getPassword(), authUser.getUserId());
        return Response.empty();
    }

    @GetMapping
    Response<ItemPageResponse>getMyItems(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String itemStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){

        Page<ItemPageResponse> itemPageResponses = itemService.getMyItems(
                authUser.getUserId(), page, size, category, itemStatus
        );
        return Response.fromPage(itemPageResponses);
    }

    @GetMapping("/v1/items/{itemId}")
    Response<ItemResponse>getItemDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId
    ){
        ItemResponse response = itemService.getItemDetail(itemId, authUser.getUserId());
        return Response.of(response);
    }
}

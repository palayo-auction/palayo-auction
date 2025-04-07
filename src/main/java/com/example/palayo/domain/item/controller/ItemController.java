package com.example.palayo.domain.item.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.item.dto.request.ItemDeleteRequest;
import com.example.palayo.domain.item.dto.request.ItemSaveRequest;
import com.example.palayo.domain.item.dto.request.ItemUpdateRequest;
import com.example.palayo.domain.item.dto.response.ItemPageResponse;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Slf4j
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

    @PatchMapping("/{itemId}/name")
    Response<ItemResponse> updateName(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemUpdateRequest request
            ){
        ItemResponse itemResponse = itemService.updateName(itemId, request.getName(), authUser.getUserId());
        return Response.of(itemResponse);
    }

    @PatchMapping("/{itemId}/content")
    Response<ItemResponse> updateContent(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemUpdateRequest request
    ){
        ItemResponse itemResponse = itemService.updateContent(itemId, request.getContent(), authUser.getUserId());
        return Response.of(itemResponse);
    }

    @PatchMapping("/{itemId}/category")
    Response<ItemResponse> updateCategory(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemUpdateRequest request
    ){
        ItemResponse itemResponse = itemService.updateCategory(itemId, request.getCategory(), authUser.getUserId());
        return Response.of(itemResponse);
    }

    @PatchMapping("/{itemId}/status")
    Response<ItemResponse> updateStatus(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ItemUpdateRequest request
    ){
        ItemResponse itemResponse = itemService.updateStatus(itemId, request.getItemStatus(), authUser.getUserId());
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

    @GetMapping("/{itemId}")
    Response<ItemResponse>getItemDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId
    ){
        ItemResponse response = itemService.getItemDetail(itemId, authUser.getUserId());
        return Response.of(response);
    }
}

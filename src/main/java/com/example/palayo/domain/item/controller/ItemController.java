package com.example.palayo.domain.item.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.item.dto.request.DeleteItemRequest;
import com.example.palayo.domain.item.dto.request.SaveItemRequest;
import com.example.palayo.domain.item.dto.request.UpdateItemRequest;
import com.example.palayo.domain.item.dto.response.PageItemResponse;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/v1/items")
    Response<ItemResponse> saveItem(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveItemRequest request
    ){
        ItemResponse itemResponse = itemService.saveItem(authUser.getUserId(), request);
        return Response.of(itemResponse);
    }

    @PatchMapping("/v1/items/{itemId}")
    Response<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateItemRequest request
            ){
        ItemResponse itemResponse = itemService.updateItem(itemId, request, authUser.getUserId());
        return Response.of(itemResponse);
    }

    @DeleteMapping("/v1/items/{itemId}")
    Response<Void> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody DeleteItemRequest request
    ){
        itemService.deleteItem(itemId, request.getPassword(), authUser.getUserId());
        return Response.empty();
    }

    @PostMapping("/v1/items/{itemId}/check")
    public Response<Void> validateItemHasImages(@PathVariable Long itemId) {
        itemService.validateItemHasImages(itemId);
        return Response.empty();
    }

    @GetMapping("/v1/items")
    Response<List<PageItemResponse>> getMyItems(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String itemStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){

        Page<PageItemResponse> itemPageResponses = itemService.getMyItems(
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

package com.example.palayo.domain.itemimage.controller;

import com.example.palayo.common.dto.AuthUser;
import com.example.palayo.common.response.Response;
import com.example.palayo.domain.itemimage.dto.request.UpdateImageUrlRequest;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.request.UpdateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.response.ItemImageResponse;
import com.example.palayo.domain.itemimage.service.ItemImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemImageController {
    private final ItemImageService itemImageService;

    @PostMapping("/v1/items/{itemId}/images")
    public Response<List<ItemImageResponse>> uploadImageInfo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId,
            @RequestBody List<CreateItemImageRequest> request) {
        List<ItemImageResponse> itemImageResponses = itemImageService.saveImages(authUser.getUserId(), itemId, request);
        return Response.of(itemImageResponses);
    }

    @PatchMapping("/v1/items/{itemId}/images")
    public Response<List<ItemImageResponse>> updateImageInfo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId,
            @RequestBody List<UpdateItemImageRequest> request) {
        List<ItemImageResponse> itemImageResponses = itemImageService.updateImageInfo(authUser.getUserId(), itemId, request);
        return Response.of(itemImageResponses);
    }

    @PatchMapping("/v1/items/{itemId}/urls")
    public Response<List<ItemImageResponse>> updateImageFiles(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId,
            @RequestBody List<UpdateImageUrlRequest> request
    ) {
        List<ItemImageResponse> updated = itemImageService.updateImageUrl(authUser.getUserId(), itemId, request);
        return Response.of(updated);
    }
}

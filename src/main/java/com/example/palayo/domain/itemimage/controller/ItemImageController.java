package com.example.palayo.domain.itemimage.controller;

import com.example.palayo.common.response.Response;
import com.example.palayo.domain.itemimage.dto.request.UpdateImageUrlRequest;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.request.UpdateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.response.ItemImageResponse;
import com.example.palayo.domain.itemimage.service.ItemImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemImageController {
    private final ItemImageService itemImageService;

    @PostMapping("/v1/items/{itemId}/images")
    public Response<List<ItemImageResponse>> uploadImageInfo(
            @PathVariable Long itemId,
            @RequestBody List<CreateItemImageRequest> request) {
        List<ItemImageResponse> itemImageResponses = itemImageService.saveImages(itemId, request);
        return Response.of(itemImageResponses);
    }

    @PatchMapping("/v1/items/{itemId}/images")
    public Response<List<ItemImageResponse>> updateImageInfo(
            @PathVariable Long itemId,
            @RequestBody List<UpdateItemImageRequest> request) {
        List<ItemImageResponse> itemImageResponses = itemImageService.updateImageInfo(itemId, request);
        return Response.of(itemImageResponses);
    }

    @GetMapping("/v1/items/{itemId}/images")
    public Response<List<ItemImageResponse>> getImages(
            @PathVariable Long itemId
    ){
       List<ItemImageResponse> responseList = itemImageService.getImagesByItemId(itemId);
       return Response.of(responseList);
    }

    @PatchMapping("/v1/items/{itemId}/urls")
    public Response<List<ItemImageResponse>> updateImageFiles(
            @PathVariable Long itemId,
            @RequestBody List<UpdateImageUrlRequest> request
    ) {
        List<ItemImageResponse> updated = itemImageService.updateImageUrl(itemId, request);
        return Response.of(updated);
    }

    @DeleteMapping("/v1/items/{itemId}/images/{imageId}")
    public Response<Void> deleteItemImage(
            @PathVariable Long itemId,
            @PathVariable Long imageId
    ) {
        itemImageService.deleteItemImage(itemId, imageId);
        return Response.empty();
    }

}

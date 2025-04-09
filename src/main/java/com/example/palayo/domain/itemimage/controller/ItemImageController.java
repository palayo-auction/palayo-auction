package com.example.palayo.domain.itemimage.controller;

import com.example.palayo.common.response.Response;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
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

    @PostMapping("/v1/items/{itemId}")
    public Response<List<ItemImageResponse>> uploadImageInfo(
            @PathVariable Long itemId,
            @RequestBody List<CreateItemImageRequest> request) {
        List<ItemImageResponse> itemImageResponses = itemImageService.saveImages(itemId, request);
        return Response.of(itemImageResponses);
    }
}

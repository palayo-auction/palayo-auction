package com.example.palayo.domain.itemimage.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.util.ItemValidator;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.request.UpdateImageUrlRequest;
import com.example.palayo.domain.itemimage.dto.request.UpdateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.response.ItemImageResponse;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.example.palayo.domain.itemimage.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ItemValidator itemValidator;

    @Transactional
    public List<ItemImageResponse> saveImages(Long userId, Long itemId, List<CreateItemImageRequest> imageRequests) {
        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

        for (CreateItemImageRequest request : imageRequests) {
            if (itemImageRepository.existsByItemAndImageUrl(item, request.getImageUrl())) {
                throw new BaseException(ErrorCode.DUPLICATE_IMAGE, request.getImageUrl());
            }
        }

        List<ItemImage> images = imageRequests.stream()
                .map(req -> ItemImage.of(req.getImageUrl(), req.getImageIndex(), item))
                .toList();

        itemImageRepository.saveAll(images);
        return images.stream()
                .map(ItemImageResponse::of)
                .toList();
    }

    @Transactional
    public List<ItemImageResponse> updateImageInfo(Long userId, Long itemId, List<UpdateItemImageRequest> requests) {
        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

        List<ItemImageResponse> updated = new ArrayList<>();

        for (UpdateItemImageRequest req : requests) {
            ItemImage image = getImageByItemAndUrl(item, req.getImageUrl());
            image.updateItemImage(req.getImageUrl(), req.getImageIndex());
            updated.add(ItemImageResponse.of(image));
        }

        return updated;
    }

    @Transactional
    public List<ItemImageResponse> updateImageUrl(Long userId,Long itemId, List<UpdateImageUrlRequest> requests) {
        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

        List<ItemImageResponse> updated = new ArrayList<>();

        for(UpdateImageUrlRequest req : requests) {
            ItemImage itemImage = getImageByItemAndUrl(item, req.getOriginalImageUrl());
            itemImage.updateItemImageUrl(req.getNewImageUrl());
            updated.add(ItemImageResponse.of(itemImage));
        }

        return updated;
    }

    @Transactional(readOnly = true)
    public List<ItemImageResponse> getImagesByItemId(Long itemId) {
        Item item = itemValidator.getValidItem(itemId);

        List<ItemImage> images = itemImageRepository.findByItemOrderByImageIndex(item);

        return images.stream()
                .map(ItemImageResponse::of)
                .toList();
    }

    private ItemImage getImageByItemAndUrl(Item item, String imageUrl) {
        return itemImageRepository.findByItemAndImageUrl(item, imageUrl)
                .orElseThrow(() -> new BaseException(ErrorCode.IMAGE_NOT_FOUND, imageUrl));
    }

}

package com.example.palayo.domain.itemimage.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
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
    private final ItemRepository itemRepository;

    @Transactional
    public List<ItemImageResponse> saveImages(Long itemId, List<CreateItemImageRequest> imageRequests) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));

        List<ItemImage> images = imageRequests.stream()
                .map(req -> ItemImage.of(req.getImageName(), req.getImageUrl(), req.getImageIndex(), item))
                .toList();

        itemImageRepository.saveAll(images);
        return images.stream()
                .map(ItemImageResponse::of)
                .toList();
    }

    @Transactional
    public List<ItemImageResponse> updateImageInfo(Long itemId, List<UpdateItemImageRequest> requests) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));

        List<ItemImageResponse> updated = new ArrayList<>();

        for (UpdateItemImageRequest req : requests) {
            ItemImage image = itemImageRepository.findByItemAndImageUrl(item, req.getImageUrl())
                    .orElseThrow(() -> new BaseException(ErrorCode.IMAGE_NOT_FOUND, req.getImageUrl()));

            image.updateItemImage(req.getImageUrl() ,req.getImageName(), req.getImageIndex());
            updated.add(ItemImageResponse.of(image));
        }

        return updated;
    }

}

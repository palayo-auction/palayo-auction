package com.example.palayo.domain.itemimage.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.itemimage.dto.request.CreateItemImageRequest;
import com.example.palayo.domain.itemimage.dto.response.ItemImageResponse;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.example.palayo.domain.itemimage.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

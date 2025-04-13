package com.example.palayo.domain.itemimage.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Transactional
    public List<ItemImageResponse> saveImages(Long itemId, List<CreateItemImageRequest> imageRequests) {
        Item item = getItemOrThrow(itemId);

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
    public List<ItemImageResponse> updateImageInfo(Long itemId, List<UpdateItemImageRequest> requests) {
        Item item = getItemOrThrow(itemId);

        List<ItemImageResponse> updated = new ArrayList<>();

        for (UpdateItemImageRequest req : requests) {
            ItemImage image = itemImageRepository.findByItemAndImageUrl(item, req.getImageUrl())
                    .orElseThrow(() -> new BaseException(ErrorCode.IMAGE_NOT_FOUND, req.getImageUrl()));

            image.updateItemImage(req.getImageUrl(), req.getImageIndex());
            updated.add(ItemImageResponse.of(image));
        }

        return updated;
    }

    @Transactional
    public List<ItemImageResponse> updateImageUrl(Long itemId, List<UpdateImageUrlRequest> requests) {
        Item item = getItemOrThrow(itemId);

        List<ItemImageResponse> updated = new ArrayList<>();

        for(UpdateImageUrlRequest req : requests) {
            ItemImage itemImage = itemImageRepository.findByItemAndImageUrl(item, req.getOriginalImageUrl())
                    .orElseThrow(() -> new BaseException(ErrorCode.IMAGE_NOT_FOUND, req.getOriginalImageUrl()));
            itemImage.updateItemImageUrl(req.getNewImageUrl());
            updated.add(ItemImageResponse.of(itemImage));
        }

        return updated;
    }

    @Transactional(readOnly = true)
    public List<ItemImageResponse> getImagesByItemId(Long itemId) {
        Item item = getItemOrThrow(itemId);

        List<ItemImage> images = itemImageRepository.findByItemOrderByImageIndex(item);

        List<ItemImageResponse> responses = new ArrayList<>();
        for (ItemImage image : images) {
            responses.add(ItemImageResponse.of(image));
        }

        return responses;
    }

    /**
     * 아이템 서비스 delete 로직에서
     *  아이템 번호에 맞는 이미지들 조회 (아이템 생성 시 이미지가 반드시 있어야 생성되므로 이미지가 없을리가 없음)
     *  찾은 이미지들의 url을 리스트로 변환
     *  업로더의 delete 호출 후 삭제
     *  itemImageRepository.deleteAll(images)
     *  순서로 감  그래서 필요 없어진거라 판단
     *  프론트 화면에서 상품 리스트가 있을 때 "수정/삭제" 버튼이 있음
     *  이때 삭제 버튼을 누르면 위 로직이 한 트랜잭션 안에 동작함
     * */
    @Transactional
    public void deleteItemImage(Long itemId, Long imageId) {
        Item item = getItemOrThrow(itemId);

        ItemImage image = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new BaseException(ErrorCode.IMAGE_NOT_FOUND, null));

        if (!image.getItem().equals(item)) {
            throw new BaseException(ErrorCode.IMAGE_NOT_FOUND, null);
        }

        itemImageRepository.delete(image);
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .filter(item -> item.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
    }
}

package com.example.palayo.domain.item.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.common.utils.S3Uploader;
import com.example.palayo.domain.item.dto.request.SaveItemRequest;
import com.example.palayo.domain.item.dto.request.UpdateItemRequest;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.dto.response.PageItemResponse;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.example.palayo.domain.itemimage.repository.ItemImageRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemImageRepository itemImageRepository;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ItemResponse saveItem(Long userId, SaveItemRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));

        Item item = Item.of(request.getName(), request.getContent(), request.getCategory(), user);
        Item savedItem = itemRepository.save(item);
        return ItemResponse.of(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, UpdateItemRequest request, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        if(request.getName() != null){
            item.updateName(request.getName());
        }

        if (request.getContent() != null){
            item.updateContent(request.getContent());
        }

        if (request.getCategory() != null){
            item.updateCategory(Category.of(request.getCategory()));
        }

        return ItemResponse.of(item);
    }

    @Transactional
    public void deleteItem(Long itemId, String password, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        if(!passwordEncoder.matches(password, item.getSeller().getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        List<ItemImage> images = itemImageRepository.findByItem(item);
        List<String> imageUrls = images.stream()
                .map(ItemImage::getImageUrl)
                .toList();
        s3Uploader.delete(imageUrls);
        itemImageRepository.deleteAll(images);
        item.markAsDeleted();
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemDetail(Long itemId, Long userId){

        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        return ItemResponse.of(item);
    }

    @Transactional(readOnly = true)
    public Page<PageItemResponse> getMyItems(Long userId, int page, int size, String category, String itemStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Category categoryEnum = category != null ? Category.of(category) : null;
        ItemStatus itemStatusEnum = itemStatus != null ? ItemStatus.of(itemStatus) : null;

        Page<Item> myItems = itemRepository.searchMyItems(userId, categoryEnum, itemStatusEnum, pageable);
        return myItems.map(PageItemResponse::of);
    }

    @Transactional
    public void validateItemHasImages(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));

        List<ItemImage> images = itemImageRepository.findByItem(item);

        if (images.isEmpty()) {
            throw new BaseException(ErrorCode.IMAGE_REQUIRED, null);
        }
    }

    private void validateOwnership(Long userId, Item item) {
        if(!item.getSeller().getId().equals(userId)){
            throw new BaseException(ErrorCode.ITEM_EDIT_FORBIDDEN, null);
        }
    }

    private void checkStatus(Item item) {
        if(item.getItemStatus() != ItemStatus.UNDER_REVIEW) {
            throw new BaseException(ErrorCode.INVALID_ITEM_STATUS_FOR_UPDATE, null);
        }
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .filter(item -> item.getDeletedAt() == null)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
    }
}
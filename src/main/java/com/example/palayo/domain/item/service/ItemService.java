package com.example.palayo.domain.item.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.dto.request.ItemSaveRequest;
import com.example.palayo.domain.item.dto.response.ItemPageResponse;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.user.entity.User;
import com.example.palayo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ItemResponse saveItem(Long userId, ItemSaveRequest request){
       User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));

       Item item = Item.of(request.getName(), request.getContent(), request.getCategory(), user);
       Item savedItem = itemRepository.save(item);
        return ItemResponse.of(savedItem);
    }

    @Transactional
    public ItemResponse updateName(Long itemId, String name, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        item.updateName(name);
        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateContent(Long itemId, String content, Long userId) {
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        item.updateContent(content);
        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateStatus(Long itemId, String itemStatus, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        item.updateStatus(ItemStatus.of(itemStatus));
        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateCategory(Long itemId, String category, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        item.updateCategory(Category.of(category));
        return ItemResponse.of(item);
    }

    @Transactional
    public void deleteItem(Long itemId, String password, Long userId){
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        checkStatus(item);

        if(!passwordEncoder.matches(password, item.getUser().getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemDetail(Long itemId, Long userId){

        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        return ItemResponse.of(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemPageResponse> getMyItems(Long userId, int page, int size, String category, String itemStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Category categoryEnum = category != null ? Category.valueOf(category.toUpperCase()) : null;
        ItemStatus itemStatusEnum = itemStatus != null ? ItemStatus.valueOf(itemStatus.toUpperCase()) : null;

        Page<Item> myItems = itemRepository.searchMyItems(userId, categoryEnum, itemStatusEnum, pageable);
        return myItems.map(ItemPageResponse::of);
    }

    private void validateOwnership(Long userId, Item item) {
        if(!item.getUser().getId().equals(userId)){
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
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
    }
}
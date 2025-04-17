package com.example.palayo.domain.item.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.common.utils.S3Uploader;
import com.example.palayo.domain.auction.repository.AuctionRepository;
import com.example.palayo.domain.elasticsearch.document.ItemDocument;
import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import com.example.palayo.domain.item.dto.request.SaveItemRequest;
import com.example.palayo.domain.item.dto.request.UpdateItemRequest;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.dto.response.PageItemResponse;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.item.repository.ItemRepository;
import com.example.palayo.domain.item.util.ItemValidator;
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
    private final ItemValidator itemValidator;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;
    private final ItemElasticSearchRepository itemElasticSearchRepository;

    @Transactional
    public ItemResponse saveItem(Long userId, SaveItemRequest request){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND, null));

        Item item = Item.of(request.getName(), request.getContent(), request.getCategory(), user);
        Item savedItem = itemRepository.save(item);

        //엘라스틱서치
        ItemDocument itemDocument = ItemDocument.of(item);
        itemElasticSearchRepository.save(itemDocument);

        return ItemResponse.of(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, UpdateItemRequest request, Long userId){
        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

        checkStatus(item);

        //엘라스틱서치
        ItemDocument itemDocument = getDocument(itemId);

        if(request.getName() != null){
            item.updateName(request.getName());
            itemDocument.updateName(request.getName());
        }

        if (request.getContent() != null){
            item.updateContent(request.getContent());
            itemDocument.updateContent(request.getContent());
        }

        if (request.getCategory() != null){
            item.updateCategory(Category.of(request.getCategory()));
            itemDocument.updateCategory(Category.of(request.getCategory()));
        }

        itemElasticSearchRepository.save(itemDocument);

        return ItemResponse.of(item);
    }

    @Transactional
    public void deleteItem(Long itemId, String password, Long userId){
        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

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

        //엘라스틱서치
        ItemDocument document = getDocument(itemId);
        itemElasticSearchRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemDetail(Long itemId, Long userId){

        Item item = itemValidator.getValidItem(itemId);
        itemValidator.validateOwnership(item, userId);

        return ItemResponse.of(item);
    }

    @Transactional(readOnly = true)
    public Page<PageItemResponse> getMyItems(Long userId, String keyword, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Category categoryEnum = category != null ? Category.of(category) : null;

        Page<Item> myItems = itemRepository.searchMyItems(userId, keyword, categoryEnum, pageable);
        return myItems.map(PageItemResponse::of);
    }

    @Transactional
    public void requireImages(Long itemId) {
        Item item = itemValidator.getValidItem(itemId);

        List<ItemImage> images = itemImageRepository.findByItem(item);

        if (images.isEmpty()) {
            throw new BaseException(ErrorCode.IMAGE_REQUIRED, null);
        }
    }

    private void checkStatus(Item item){
        if(item.getStatus() != ItemStatus.AVAILABLE) {
            throw new BaseException(ErrorCode.INVALID_ITEM_STATUS_FOR_UPDATE, null);
        }
    }

    private ItemDocument getDocument(Long documentId) {
        return itemElasticSearchRepository.findById(documentId)
            .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
    }
}
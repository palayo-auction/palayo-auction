package com.example.palayo.domain.item.service;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.elasticsearch.document.ItemDocument;
import com.example.palayo.domain.elasticsearch.repository.ItemElasticSearchRepository;
import com.example.palayo.domain.item.dto.request.SaveItemRequest;
import com.example.palayo.domain.item.dto.request.UpdateItemRequest;
import com.example.palayo.domain.item.dto.response.PageItemResponse;
import com.example.palayo.domain.item.dto.response.ItemResponse;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
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
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

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
        Item item = getItemOrThrow(itemId);

        validateOwnership(userId, item);

        if(!passwordEncoder.matches(password, item.getSeller().getPassword())) {
            throw new BaseException(ErrorCode.PASSWORD_MISMATCH, null);
        }

        itemRepository.delete(item);

        //엘라스틱서치
        ItemDocument document = getDocument(itemId);
        itemElasticSearchRepository.delete(document);
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

        Page<Item> myItems = itemRepository.searchMyItems(userId, categoryEnum, pageable);
        return myItems.map(PageItemResponse::of);
    }

    private void validateOwnership(Long userId, Item item) {
        if(!item.getSeller().getId().equals(userId)){
            throw new BaseException(ErrorCode.ITEM_EDIT_FORBIDDEN, null);
        }
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
    }

    private ItemDocument getDocument(Long documentId) {
        return itemElasticSearchRepository.findById(documentId).get();
    }
}
package com.example.palayo.domain.item.repository;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> searchMyItems(Long userId, String keyword, Category category, Pageable pageable);
}

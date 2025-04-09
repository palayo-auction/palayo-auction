package com.example.palayo.domain.itemimage.repository;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemImageRepository extends JpaRepository<ItemImage,Long> {
    Optional<ItemImage> findByItemAndImageUrl(Item item, String imageUrl);

}

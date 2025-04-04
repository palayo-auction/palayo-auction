package com.example.palayo.domain.itemimage.entity;

import com.example.palayo.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_images")
public class ItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer imageIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public static ItemImage of(String imageName, String imageUrl, Integer imageIndex, Item item){
        ItemImage itemImage = new ItemImage();
        itemImage.imageName = imageName;
        itemImage.imageUrl = imageUrl;
        itemImage.imageIndex = imageIndex;
        itemImage.item = item;
        return itemImage;
    }
}

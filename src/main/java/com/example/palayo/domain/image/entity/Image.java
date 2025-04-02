package com.example.palayo.domain.image.entity;

import com.example.palayo.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "images")
public class Image {
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

    public static Image of(String imageName, String imageUrl, Integer imageIndex, Item item){
        Image image = new Image();
        image.imageName = imageName;
        image.imageUrl = imageUrl;
        image.imageIndex = imageIndex;
        image.item = item;
        return image;
    }
}

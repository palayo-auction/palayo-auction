package com.example.palayo.domain.itemimage.dto.response;

import com.example.palayo.domain.itemimage.entity.ItemImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemImageResponse {
    private final Long imageId;
    private final String imageName;
    private final String imageUrl;
    private final Integer imageIndex;

    public static ItemImageResponse of(ItemImage itemImage) {
        return new ItemImageResponse(
                itemImage.getId(),
                itemImage.getImageName(),
                itemImage.getImageUrl(),
                itemImage.getImageIndex()
        );
    }
}

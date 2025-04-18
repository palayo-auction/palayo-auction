package com.example.palayo.domain.item.dto.response;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageItemResponse {
    private Long id;
    private String name;
    private String content;
    private Category category;
    private String thumbnailUrl;

    public static PageItemResponse of(Item item, String thumbnailUrl){
        return new PageItemResponse(
                item.getId(),
                item.getName(),
                item.getContent(),
                item.getCategory(),
                thumbnailUrl
        );
    }
}

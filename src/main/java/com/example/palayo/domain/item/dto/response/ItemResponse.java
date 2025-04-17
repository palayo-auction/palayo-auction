package com.example.palayo.domain.item.dto.response;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemResponse {
    private Long id;
    private String name;
    private String userNickname;
    private String content;
    private Category category;

    public static ItemResponse of(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getSeller().getNickname(),
                item.getContent(),
                item.getCategory()
        );
    }
}

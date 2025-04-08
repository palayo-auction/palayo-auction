package com.example.palayo.domain.user.dto.response;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserItemResponse {
    private Long id;
    private String name;
    private String content;
    private Category category;
    private ItemStatus itemStatus;

    public static UserItemResponse of(Item item) {
        return new UserItemResponse(
                item.getId(),
                item.getName(),
                item.getContent(),
                item.getCategory(),
                item.getItemStatus()
        );
    }
}

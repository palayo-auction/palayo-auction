package com.example.palayo.domain.item.dto.response;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemPageResponse {
    private Long id;
    private String name;
    private String content;
    private Category category;
    private ItemStatus itemStatus;

    public static ItemPageResponse of(Item item){
        return new ItemPageResponse(
                item.getId(),
                item.getName(),
                item.getContent(),
                item.getCategory(),
                item.getItemStatus()
        );
    }
}

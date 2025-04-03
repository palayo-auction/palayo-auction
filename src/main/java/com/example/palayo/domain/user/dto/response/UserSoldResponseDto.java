package com.example.palayo.domain.user.dto.response;

import com.example.palayo.domain.item.entity.Item;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSoldResponseDto {
    private Page<Item> items;

    public static UserSoldResponseDto of(Page<Item> items) {
        return new UserSoldResponseDto(items);
    }
}

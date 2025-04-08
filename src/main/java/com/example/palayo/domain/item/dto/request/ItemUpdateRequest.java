package com.example.palayo.domain.item.dto.request;

import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ItemUpdateRequest {
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    private String name;
    @Size(max = 100, message = "내용은 100자 이하로 입력해주세요.")
    private String content;
    private String category;
}

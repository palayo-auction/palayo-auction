package com.example.palayo.domain.item.dto.request;

import com.example.palayo.domain.item.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ItemSaveRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotNull
    private Category category;
}

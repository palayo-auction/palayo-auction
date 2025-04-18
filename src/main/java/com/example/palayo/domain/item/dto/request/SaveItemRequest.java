package com.example.palayo.domain.item.dto.request;

import com.example.palayo.domain.item.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SaveItemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotNull
    private String category;
}

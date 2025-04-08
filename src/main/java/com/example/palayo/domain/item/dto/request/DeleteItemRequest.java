package com.example.palayo.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteItemRequest {
    @NotBlank
    private String password;
}

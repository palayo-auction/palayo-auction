package com.example.palayo.domain.itemimage.dto.request;

import lombok.Getter;

@Getter
public class UpdateImageUrlRequest {
    private String originalImageUrl;
    private String newImageUrl;
}


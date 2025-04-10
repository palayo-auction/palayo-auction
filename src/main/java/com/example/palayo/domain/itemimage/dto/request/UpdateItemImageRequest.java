package com.example.palayo.domain.itemimage.dto.request;

import lombok.Getter;

@Getter
public class UpdateItemImageRequest {
    private String imageUrl;
    private String imageName;
    private Integer imageIndex;
}


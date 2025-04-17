package com.example.palayo.domain.itemimage.dto.request;

import lombok.Getter;

@Getter
public class UpdateImageUrlRequest {
    private String targetImageUrl; // <-- 삭제할 url
    private String newImageUrl; // <-- 새롭게 추가할 url
}

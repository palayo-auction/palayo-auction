package com.example.palayo.domain.item.enums;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;

import java.util.Arrays;

public enum Category {
    ELECTRONICS,    // 전자제품
    FURNITURE,      // 가구
    FASHION,        // 의류/악세서리
    BOOKS,          // 도서
    ART,            // 예술품
    SPORTS,         // 스포츠용품
    TOYS,           // 장난감
    ETC;             // 기타

    public static Category of(String category){
        return Arrays.stream(Category.values())
                .filter(c -> c.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND, category));
    }
}

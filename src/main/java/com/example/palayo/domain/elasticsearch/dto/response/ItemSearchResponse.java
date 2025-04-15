package com.example.palayo.domain.elasticsearch.dto.response;

import com.example.palayo.domain.elasticsearch.document.ItemDocument;

public record ItemSearchResponse (
        Long id,
        String name,
        String content,
        String category,
        String ItemStatus,
        Long sellerId
) {
    public static ItemSearchResponse of(ItemDocument item) {
        return new ItemSearchResponse(
                item.getId(),
                item.getName(),
                item.getContent(),
                item.getCategory().name(),
                item.getItemStatus().name(),
                item.getSellerId()
        );
    }
}



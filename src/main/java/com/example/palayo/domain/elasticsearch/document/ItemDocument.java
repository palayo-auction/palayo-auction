package com.example.palayo.domain.elasticsearch.document;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "items", writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "elastic/item-setting.json")
@Mapping(mappingPath = "elastic/item-mapping.json")
public class ItemDocument {
    @Id
    private Long id;

    private String name;

    private String content;

    private Category category;

    private Long sellerId;

    public static ItemDocument of(Item item) {
        ItemDocument itemDocument = new ItemDocument();
        itemDocument.id = item.getId();
        itemDocument.name = item.getName();
        itemDocument.content = item.getContent();
        itemDocument.category = item.getCategory();
        itemDocument.sellerId = item.getSeller().getId();
        return itemDocument;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}

package com.example.palayo.domain.elasticsearch.document;

import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "items")
@Setting(settingPath = "elastic/item-setting.json")
@Mapping(mappingPath = "elastic/item-mapping.json")
public class ItemDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private Category category;

    @Field(type = FieldType.Keyword)
    private ItemStatus itemStatus;

    @Field(type = FieldType.Text)
    private Long sellerId;

    @Field(type = FieldType.Nested)
    private List<ItemImage> itemImages = new ArrayList<>();

    public static ItemDocument of(Long id, String name, String content, Category category, ItemStatus itemStatus, Long sellerId) {
        ItemDocument item = new ItemDocument();
        item.id = id;
        item.name = name;
        item.content = content;
        item.category = category;
        item.itemStatus = itemStatus;
        item.sellerId = sellerId;
        return item;
    }
}

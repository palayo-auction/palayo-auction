package com.example.palayo.domain.item.entity;

import com.example.palayo.common.entity.BaseEntity;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "items")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

//    유저 추가 시 of에도 유저 포함 해야함
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    public static Item of(String name, String content, Category category){
        Item item = new Item();
        item.name = name;
        item.content = content;
        item.category = category;
        item.itemStatus = ItemStatus.UNDER_REVIEW;
        return item;
    }
}

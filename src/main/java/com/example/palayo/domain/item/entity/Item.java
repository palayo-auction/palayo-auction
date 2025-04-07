package com.example.palayo.domain.item.entity;

import com.example.palayo.common.entity.BaseEntity;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.example.palayo.domain.user.entity.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User user;

    public static Item of(String name, String content, Category category, User user){
        Item item = new Item();
        item.user = user;
        item.name = name;
        item.content = content;
        item.category = category;
        item.itemStatus = ItemStatus.UNDER_REVIEW;
        return item;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}

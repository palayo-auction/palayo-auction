package com.example.palayo.domain.item.entity;

import com.example.palayo.common.entity.BaseEntity;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.itemimage.entity.ItemImage;
import com.example.palayo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("imageIndex ASC")
    private List<ItemImage> itemImages = new ArrayList<>();

    private LocalDateTime deletedAt;

    public static Item of(String name, String content, Category category, User seller){
        Item item = new Item();
        item.seller = seller;
        item.name = name;
        item.content = content;
        item.category = category;
        return item;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
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
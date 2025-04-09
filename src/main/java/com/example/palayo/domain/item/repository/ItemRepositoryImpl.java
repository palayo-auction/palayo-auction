package com.example.palayo.domain.item.repository;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.example.palayo.domain.item.enums.ItemStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.palayo.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchMyItems(Long userId, Category category, ItemStatus itemStatus, Pageable pageable) {

        List<Item> results = queryFactory
                .selectFrom(item)
                .where(
                        item.seller.id.eq(userId),
                        eqCategory(category),
                        eqItemStatus(itemStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .where(
                        item.seller.id.eq(userId),
                        eqCategory(category),
                        eqItemStatus(itemStatus)
                );

        return PageableExecutionUtils.getPage(results, pageable,
                countQuery::fetchOne);
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? item.category.eq(category) : null;
    }

    private BooleanExpression eqItemStatus(ItemStatus status) {
        return status != null ? item.itemStatus.eq(status) : null;
    }
}

package com.example.palayo.domain.item.repository;

import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.enums.Category;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.palayo.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchMyItems(Long userId, String keyword, Category category, Pageable pageable) {

        List<Item> results = queryFactory
                .selectFrom(item)
                .where(
                        item.seller.id.eq(userId),
                        containsKeyword(keyword),
                        eqCategory(category)
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
                        containsKeyword(keyword),
                        eqCategory(category)
                );

        return PageableExecutionUtils.getPage(results, pageable,
                countQuery::fetchOne);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return item.name.containsIgnoreCase(keyword)
                .or(item.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? item.category.eq(category) : null;
    }
}

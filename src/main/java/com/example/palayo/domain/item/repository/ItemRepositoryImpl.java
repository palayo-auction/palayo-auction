package com.example.palayo.domain.item.repository;

import com.example.palayo.domain.auction.enums.AuctionStatus;
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
import static com.example.palayo.domain.auction.entity.QAuction.auction;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchMyItems(Long userId, Category category, AuctionStatus auctionStatus, Pageable pageable) {

        List<Item> results = queryFactory
                .selectFrom(item)
                .leftJoin(auction).on(auction.item.eq(item),auction.deletedAt.isNull())
                .where(
                        item.seller.id.eq(userId),
                        item.deletedAt.isNull(),
                        eqCategory(category),
                        eqAuctionStatus(auctionStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .leftJoin(auction).on(auction.item.eq(item), auction.deletedAt.isNull())
                .where(
                        item.seller.id.eq(userId),
                        item.deletedAt.isNull(),
                        eqCategory(category),
                        eqAuctionStatus(auctionStatus)
                );

        return PageableExecutionUtils.getPage(results, pageable,
                countQuery::fetchOne);
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? item.category.eq(category) : null;
    }

    private BooleanExpression eqAuctionStatus(AuctionStatus status) {
        return status != null ? auction.status.eq(status): null;
    }
}

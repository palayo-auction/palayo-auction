package com.example.palayo.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = 1133574700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuction auction = new QAuction("auction");

    public final NumberPath<Integer> bidIncrement = createNumber("bidIncrement", Integer.class);

    public final NumberPath<Integer> buyoutPrice = createNumber("buyoutPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> currentPrice = createNumber("currentPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.palayo.domain.item.entity.QItem item;

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> startingPrice = createNumber("startingPrice", Integer.class);

    public final EnumPath<com.example.palayo.domain.auction.enums.AuctionStatus> status = createEnum("status", com.example.palayo.domain.auction.enums.AuctionStatus.class);

    public final DateTimePath<java.time.LocalDateTime> successAt = createDateTime("successAt", java.time.LocalDateTime.class);

    public final com.example.palayo.domain.user.entity.QUser winningBidder;

    public QAuction(String variable) {
        this(Auction.class, forVariable(variable), INITS);
    }

    public QAuction(Path<? extends Auction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuction(PathMetadata metadata, PathInits inits) {
        this(Auction.class, metadata, inits);
    }

    public QAuction(Class<? extends Auction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.example.palayo.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.winningBidder = inits.isInitialized("winningBidder") ? new com.example.palayo.domain.user.entity.QUser(forProperty("winningBidder")) : null;
    }

}


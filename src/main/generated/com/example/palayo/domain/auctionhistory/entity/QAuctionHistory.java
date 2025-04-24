package com.example.palayo.domain.auctionhistory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionHistory is a Querydsl query type for AuctionHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionHistory extends EntityPathBase<AuctionHistory> {

    private static final long serialVersionUID = -638770840L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionHistory auctionHistory = new QAuctionHistory("auctionHistory");

    public final com.example.palayo.domain.auction.entity.QAuction auction;

    public final com.example.palayo.domain.user.entity.QUser bidder;

    public final NumberPath<Integer> bidPrice = createNumber("bidPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAuctionHistory(String variable) {
        this(AuctionHistory.class, forVariable(variable), INITS);
    }

    public QAuctionHistory(Path<? extends AuctionHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionHistory(PathMetadata metadata, PathInits inits) {
        this(AuctionHistory.class, metadata, inits);
    }

    public QAuctionHistory(Class<? extends AuctionHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.example.palayo.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.bidder = inits.isInitialized("bidder") ? new com.example.palayo.domain.user.entity.QUser(forProperty("bidder")) : null;
    }

}


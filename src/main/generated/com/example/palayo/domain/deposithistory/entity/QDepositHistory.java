package com.example.palayo.domain.deposithistory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepositHistory is a Querydsl query type for DepositHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepositHistory extends EntityPathBase<DepositHistory> {

    private static final long serialVersionUID = 8431154L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDepositHistory depositHistory = new QDepositHistory("depositHistory");

    public final com.example.palayo.domain.auction.entity.QAuction auction;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> deposit = createNumber("deposit", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.palayo.domain.deposithistory.enums.DepositStatus> status = createEnum("status", com.example.palayo.domain.deposithistory.enums.DepositStatus.class);

    public final com.example.palayo.domain.user.entity.QUser user;

    public QDepositHistory(String variable) {
        this(DepositHistory.class, forVariable(variable), INITS);
    }

    public QDepositHistory(Path<? extends DepositHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDepositHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDepositHistory(PathMetadata metadata, PathInits inits) {
        this(DepositHistory.class, metadata, inits);
    }

    public QDepositHistory(Class<? extends DepositHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.example.palayo.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.user = inits.isInitialized("user") ? new com.example.palayo.domain.user.entity.QUser(forProperty("user")) : null;
    }

}


package com.example.palayo.domain.dib.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDib is a Querydsl query type for Dib
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDib extends EntityPathBase<Dib> {

    private static final long serialVersionUID = 377666284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDib dib = new QDib("dib");

    public final com.example.palayo.domain.auction.entity.QAuction auction;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.palayo.domain.user.entity.QUser user;

    public QDib(String variable) {
        this(Dib.class, forVariable(variable), INITS);
    }

    public QDib(Path<? extends Dib> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDib(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDib(PathMetadata metadata, PathInits inits) {
        this(Dib.class, metadata, inits);
    }

    public QDib(Class<? extends Dib> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.example.palayo.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.user = inits.isInitialized("user") ? new com.example.palayo.domain.user.entity.QUser(forProperty("user")) : null;
    }

}


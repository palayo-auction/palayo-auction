package com.example.palayo.domain.pointhistory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPointHistories is a Querydsl query type for PointHistories
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointHistories extends EntityPathBase<PointHistories> {

    private static final long serialVersionUID = -572229844L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPointHistories pointHistories = new QPointHistories("pointHistories");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.palayo.domain.user.enums.PointType> pointType = createEnum("pointType", com.example.palayo.domain.user.enums.PointType.class);

    public final com.example.palayo.domain.user.entity.QUser user;

    public QPointHistories(String variable) {
        this(PointHistories.class, forVariable(variable), INITS);
    }

    public QPointHistories(Path<? extends PointHistories> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPointHistories(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPointHistories(PathMetadata metadata, PathInits inits) {
        this(PointHistories.class, metadata, inits);
    }

    public QPointHistories(Class<? extends PointHistories> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.palayo.domain.user.entity.QUser(forProperty("user")) : null;
    }

}


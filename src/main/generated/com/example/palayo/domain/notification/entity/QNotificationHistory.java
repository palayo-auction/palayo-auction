package com.example.palayo.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationHistory is a Querydsl query type for NotificationHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationHistory extends EntityPathBase<NotificationHistory> {

    private static final long serialVersionUID = -643686280L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationHistory notificationHistory = new QNotificationHistory("notificationHistory");

    public final StringPath body = createString("body");

    public final MapPath<String, String, StringPath> data = this.<String, String, StringPath>createMap("data", String.class, String.class, StringPath.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSent = createBoolean("isSent");

    public final DateTimePath<java.time.LocalDateTime> scheduledAt = createDateTime("scheduledAt", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final StringPath token = createString("token");

    public final EnumPath<com.example.palayo.domain.notification.enums.NotificationType> type = createEnum("type", com.example.palayo.domain.notification.enums.NotificationType.class);

    public final com.example.palayo.domain.user.entity.QUser user;

    public QNotificationHistory(String variable) {
        this(NotificationHistory.class, forVariable(variable), INITS);
    }

    public QNotificationHistory(Path<? extends NotificationHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationHistory(PathMetadata metadata, PathInits inits) {
        this(NotificationHistory.class, metadata, inits);
    }

    public QNotificationHistory(Class<? extends NotificationHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.palayo.domain.user.entity.QUser(forProperty("user")) : null;
    }

}


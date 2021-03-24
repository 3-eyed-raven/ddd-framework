package net.jsrbc.repository.mongodb;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用基于Mongodb的事件存储
 * @author ZZZ on 2021-03-24 16:42
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MongoEventStore.class)
public @interface EnableMongodbEventStore {}

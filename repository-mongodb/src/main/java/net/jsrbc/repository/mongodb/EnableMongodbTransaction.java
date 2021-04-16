package net.jsrbc.repository.mongodb;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用mongodb事务
 * @author ZZZ on 2021/4/16 15:27
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MongoTransactionConfigurer.class)
public @interface EnableMongodbTransaction {}

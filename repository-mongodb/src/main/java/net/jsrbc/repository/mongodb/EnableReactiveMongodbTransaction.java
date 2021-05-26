package net.jsrbc.repository.mongodb;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用响应式mongodb事务
 * @author ZZZ on 2021/5/26 10:26
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ReactiveMongoTransactionConfigurer.class)
public @interface EnableReactiveMongodbTransaction {}

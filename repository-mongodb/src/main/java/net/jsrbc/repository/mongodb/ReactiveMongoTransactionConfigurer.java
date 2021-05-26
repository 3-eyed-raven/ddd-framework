package net.jsrbc.repository.mongodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * 响应式Mongo事务配置
 * @author ZZZ on 2021/5/26 10:25
 * @version 1.0
 */
@ConditionalOnBean(ReactiveMongoConfigurer.class)
@Configuration
public class ReactiveMongoTransactionConfigurer {

    @Bean
    public ReactiveMongoTransactionManager reactiveMongoTransactionManager(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory) {
        return new ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory);
    }

    @Bean
    public ReactiveTransactionExecutor reactiveTransactionExecutor(ReactiveMongoTemplate reactiveMongoTemplate,
                                                                   ReactiveMongoTransactionManager reactiveMongoTransactionManager) {
        return new ReactiveTransactionExecutor(reactiveMongoTemplate, reactiveMongoTransactionManager);
    }
}

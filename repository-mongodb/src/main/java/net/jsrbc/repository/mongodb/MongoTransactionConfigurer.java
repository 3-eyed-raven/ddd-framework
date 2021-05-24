package net.jsrbc.repository.mongodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * mongodb事务配置
 * @author ZZZ on 2021/4/16 15:29
 * @version 1.0
 */
@ConditionalOnBean(MongoConfigurer.class)
@Configuration
public class MongoTransactionConfigurer {

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public TransactionExecutor transactionExecutor(MongoTemplate mongoTemplate, MongoTransactionManager mongoTransactionManager) {
        return new TransactionExecutor(mongoTemplate, mongoTransactionManager);
    }
}

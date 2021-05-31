package net.jsrbc.repository.mongodb;

import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

/**
 * 响应式事务执行器
 * @author ZZZ on 2021/5/26 10:31
 * @version 1.0
 */
public class ReactiveTransactionExecutor {

    /** 响应式事务操作 */
    private final TransactionalOperator transactionalOperator;

    /**
     * 事务执行操作
     * @param executor 执行回调
     */
    public <T> Mono<T> execute(Executor<T> executor) {
        return executor.execute()
                .as(this.transactionalOperator::transactional);
    }

    @FunctionalInterface
    public interface Executor<T> {
        Mono<T> execute();
    }

    public ReactiveTransactionExecutor(ReactiveMongoTemplate reactiveMongoTemplate,
                                       ReactiveMongoTransactionManager reactiveMongoTransactionManager) {
        reactiveMongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
        this.transactionalOperator = TransactionalOperator.create(reactiveMongoTransactionManager);
    }
}

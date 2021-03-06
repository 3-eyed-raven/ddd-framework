package net.jsrbc.repository.mongodb;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务执行操作
 * @author ZZZ on 2021-03-24 17:16
 * @version 1.0
 */
public class TransactionExecutor {

    /** 事务模板 */
    private final TransactionTemplate transactionTemplate;

    /**
     * 事务执行操作
     * @param executor 执行回调
     */
    public <T> T execute(Executor<T> executor) {
        return this.transactionTemplate.execute(status -> {
            try {
                return executor.execute();
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FunctionalInterface
    public interface Executor<T> {
        T execute() throws Throwable;
    }

    public TransactionExecutor(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) {
        mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }
}

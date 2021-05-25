package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.function.Consumer;

/**
 * mongodb线程/进程安全操作，删除操作采取逻辑删除
 * @author ZZZ on 2021/2/23 10:38
 * @version 1.0
 */
public interface SafeMongoOperations {

    /**
     * 检查是否存在
     * @param query 查询条件
     * @param <T> 聚合类型参数
     * @return 是否包含
     */
    <T extends Aggregate> boolean exists(Query query, Class<T> aggregateClass);

    /**
     * 查询多个聚合，不包含被删除的聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 聚合集合
     */
    <T extends Aggregate> List<T> find(Query query, Class<T> aggregateClass);

    /**
     * 查询多个被删除的聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 聚合集合
     */
    <T extends Aggregate> List<T> findDeleted(Query query, Class<T> aggregateClass);

    /**
     * 统计数量，不包含被删除的聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 聚合数量
     */
    <T extends Aggregate> long count(Query query, Class<T> aggregateClass);

    /**
     * 统计被删除的聚合数量
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 聚合数量
     */
    <T extends Aggregate> long countDeleted(Query query, Class<T> aggregateClass);

    /**
     * 遍历数据，底层采用流的方式遍历，不包含被删除的聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param consumer 聚合消费者
     * @param <T> 聚合类型参数
     */
    <T extends Aggregate> void forEach(Query query, Class<T> aggregateClass, Consumer<T> consumer);

    /**
     * 通过ID查询
     * @param id ID
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 查询结果
     */
    <T extends Aggregate> T findById(String id, Class<T> aggregateClass);

    /**
     * 查找一个聚合，不包含被删除的聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 单个聚合
     */
    <T extends Aggregate> T findOne(Query query, Class<T> aggregateClass);

    /**
     * 查找一个被删除聚合
     * @param query 查询条件
     * @param aggregateClass 聚合类型
     * @param <T> 聚合类型参数
     * @return 单个聚合
     */
    <T extends Aggregate> T findOneDeleted(Query query, Class<T> aggregateClass);

    /**
     * 并发安全的保存操作，通过乐观锁实现，会去比较聚合的版本号
     * @param aggregate 聚合
     * @throws InvalidDataException 版本冲突异常
     */
    void save(Aggregate aggregate);

    /**
     * 删除一个聚合，使用逻辑删除
     */
    void remove(Aggregate aggregate);

    /**
     * 还原一个被删除的聚合
     */
    void restore(Aggregate aggregate);
}

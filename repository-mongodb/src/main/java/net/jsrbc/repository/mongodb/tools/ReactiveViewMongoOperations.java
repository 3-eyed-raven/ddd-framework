package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.view.View;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应式视图mongodb数据库操作
 * @author ZZZ on 2021/3/17 21:17
 * @version 1.0
 */
public interface ReactiveViewMongoOperations {
    /**
     * 检查是否存在
     * @param id 视图主键
     * @param version 版本
     * @param viewName 视图名称，对应视图类名
     * @return 是否被更新过
     */
    Mono<CheckResult> exists(String id, Long version, String viewName);

    /**
     * 查询多个视图，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 视图集合
     */
    <T extends View> Flux<T> find(Query query, Class<T> viewClass);

    /**
     * 统计数量，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 视图数量
     */
    <T extends View> Mono<Long> count(Query query, Class<T> viewClass);

    /**
     * 查找一个视图，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 单个视图
     */
    <T extends View> Mono<T> findOne(Query query, Class<T> viewClass);

    /**
     * 并发安全的保存操作，通过乐观锁实现，会去比较视图的版本号
     * @param view 视图
     */
    Mono<Void> save(View view);

    /**
     * 删除视图
     * @param id 主键
     * @param viewClass 视图类
     */
    <T extends View> Mono<Void> remove(String id, Class<T> viewClass);

    /**
     * 更新多个文档
     * @param query 查询条件
     * @param update 更新内容
     * @param viewClass 视图类
     */
    <T extends View> Mono<Void> update(Query query, Update update, Class<T> viewClass);
}

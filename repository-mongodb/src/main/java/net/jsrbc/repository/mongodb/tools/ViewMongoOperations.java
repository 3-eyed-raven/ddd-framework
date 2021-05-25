package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.dto.PageDTO;
import net.jsrbc.ddd.core.view.View;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.function.Consumer;

/**
 * 视图的mongodb数据库操作
 * @author ZZZ on 2021/3/17 14:58
 * @version 1.0
 */
public interface ViewMongoOperations {
    /**
     * 检查是否存在
     * @param query 查询条件
     * @param viewName 视图名称，对应视图类名
     * @return 是否被更新过
     */
    <T extends View> boolean exists(Query query, String viewName);

    /**
     * 查询多个视图，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 视图集合
     */
    <T extends View> List<T> find(Query query, Class<T> viewClass);

    /**
     * 分页查询
     * @param criteria 查询条件
     * @param current 当前页，从1开始
     * @param pageSize 页面尺寸
     * @param orders 排序策略
     * @param <T> 视图类型参数
     * @return 分页数据
     */
    <T extends View> PageDTO findPagination(Criteria criteria, int current, int pageSize, Class<T> viewClass, Sort.Order... orders);

    /**
     * 统计数量，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 视图数量
     */
    <T extends View> long count(Query query, Class<T> viewClass);

    /**
     * 遍历数据，底层采用流的方式遍历，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param consumer 视图消费者
     * @param <T> 视图类型参数
     */
    <T extends View> void forEach(Query query, Class<T> viewClass, Consumer<T> consumer);

    /**
     * 通过ID查询
     * @param id ID
     * @param <T> 聚合类型参数
     * @param viewClass 视图类型
     * @return 查询结果
     */
    <T extends View> T findById(String id, Class<T> viewClass);

    /**
     * 查找一个视图，不包含被删除的视图
     * @param query 查询条件
     * @param viewClass 视图类型
     * @param <T> 视图类型参数
     * @return 单个视图
     */
    <T extends View> T findOne(Query query, Class<T> viewClass);

    /**
     * 并发安全的保存操作，通过乐观锁实现，会去比较视图的版本号
     * @param view 视图
     */
    void save(View view);

    /**
     * 删除视图
     * @param id 主键
     * @param viewClass 视图类
     */
    <T extends View> void remove(String id, Class<T> viewClass);

    /**
     * 删除视图，注意：会删除多条记录
     * @param query 查询条件
     * @param viewClass 视图类
     */
    <T extends View> void remove(Query query, Class<T> viewClass);

    /**
     * 更新多个文档
     * @param query 查询条件
     * @param update 更新内容
     * @param viewClass 视图类
     */
    <T extends View> void update(Query query, Update update, Class<T> viewClass);
}

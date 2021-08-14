package net.jsrbc.ddd.core.usecase;

import net.jsrbc.ddd.core.aggregate.Aggregate;
import net.jsrbc.ddd.core.repository.Repository;

import static net.jsrbc.ddd.core.old.utils.Validator.*;

/**
 * 删除用例
 * @author ZZZ on 2021/6/7 16:10
 * @version 1.0
 */
public abstract class RemoveUseCase<T extends Aggregate> {

    private final Repository<T> repository;

    /**
     * 检查前置条件
     */
    protected void beforeRemove(T aggregate) {}

    /**
     * 聚合删除后的操作
     * @param aggregate 聚合
     */
    protected void afterRemove(T aggregate) {}

    /**
     * 删除聚合
     * @param id 聚合ID
     * @return 被删除的聚合
     */
    public T remove(String id) {
        // 1、校验
        notNull(id, "id 不能为空");
        // 2、查询被删除聚合
        T aggregate = this.repository.findById(id);
        // 3、校验聚合
        notNull(aggregate, "对象不存在");
        // 4、删除的前置条件检查
        this.beforeRemove(aggregate);
        // 5、删除聚合
        this.repository.remove(aggregate);
        // 6、发起通知
        aggregate.notifyRemoved();
        // 7、聚合已经删除后的操作
        this.afterRemove(aggregate);
        // 8、返回
        return aggregate;
    }

    public RemoveUseCase(Repository<T> repository) {
        this.repository = repository;
    }
}

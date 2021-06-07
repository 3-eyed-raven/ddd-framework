package net.jsrbc.ddd.core.model.usecase;

import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import net.jsrbc.ddd.core.model.repository.Repository;

import static net.jsrbc.ddd.core.utils.Validator.*;

/**
 * 聚合删除用例
 * @author ZZZ on 2021/6/7 16:10
 * @version 1.0
 */
public abstract class AggregateRemover<T extends Aggregate> {

    private final Repository<T> repository;

    /**
     * 检查前置条件
     */
    protected void preconditionCheck() {}

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
        this.preconditionCheck();
        // 5、删除聚合
        this.repository.remove(aggregate);
        // 6、发起通知
        aggregate.notifyRemoved();
        // 7、返回
        return aggregate;
    }

    public AggregateRemover(Repository<T> repository) {
        this.repository = repository;
    }
}

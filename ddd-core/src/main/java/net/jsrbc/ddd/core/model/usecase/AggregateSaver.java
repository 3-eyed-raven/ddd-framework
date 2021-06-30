package net.jsrbc.ddd.core.model.usecase;

import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import net.jsrbc.ddd.core.model.aggregate.TreeAggregate;
import net.jsrbc.ddd.core.model.repository.Repository;
import net.jsrbc.ddd.core.model.service.CircularReferenceCheckService;

import static net.jsrbc.ddd.core.utils.Validator.*;

/**
 * @author ZZZ on 2021/6/7 16:01
 * @version 1.0
 */
public abstract class AggregateSaver<T extends Aggregate> {

    private final Repository<T> repository;

    /**
     * 聚合映射
     * @param aggregate 聚合
     * @return 映射后的聚合
     */
    protected T map(T aggregate) {
        return aggregate;
    }

    /**
     * 聚合保存后的处理
     */
    protected void doOnSaved(T aggregate) {}

    /**
     * 保存聚合
     * @param aggregate 聚合
     * @return 被保存的聚合
     */
    @SuppressWarnings("unchecked")
    public T save(T aggregate) {
        // 1、聚合的各类校验
        notNull(aggregate, "aggregate 不能为空");
        aggregate.validate();
        aggregate.setIdIfAbsent(this.repository::nextIdentifier);
        // 2、如果为树结构数据，则补充校验
        if (aggregate instanceof TreeAggregate) {
            CircularReferenceCheckService.check((TreeAggregate) aggregate, (Repository<? extends TreeAggregate>) this.repository);
        }
        // 3、聚合映射
        aggregate = map(aggregate);
        // 4、聚合保存
        this.repository.save(aggregate);
        // 5、聚合保存通知
        aggregate.notifySaved();
        // 6、保存后的一些自定义操作
        this.doOnSaved(aggregate);
        // 7、被保存聚合返回
        return aggregate;
    }

    public AggregateSaver(Repository<T> repository) {
        this.repository = repository;
    }
}

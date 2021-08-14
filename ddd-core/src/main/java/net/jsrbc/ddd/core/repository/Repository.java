package net.jsrbc.ddd.core.repository;

import net.jsrbc.ddd.core.aggregate.Aggregate;

/**
 * 仓库
 * @author ZZZ on 2021/3/10 14:31
 * @version 1.0
 */
public interface Repository<T extends Aggregate> {
    /**
     * 生成下一个唯一标识
     * @return 唯一标识
     */
    String nextIdentifier();

    /**
     * 通过ID查找聚合
     * @param id 聚合ID
     * @return 聚合
     */
    T findById(String id);

    /**
     * 保存聚合
     * @param aggregate 聚合
     */
    void save(T aggregate);

    /**
     * 删除聚合
     * @param aggregate 聚合
     */
    void remove(T aggregate);
}

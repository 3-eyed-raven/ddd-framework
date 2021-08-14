package net.jsrbc.ddd.core.aggregate;

/**
 * 树形聚合
 * @author ZZZ on 2021/5/27 16:18
 * @version 1.0
 */
public abstract class TreeAggregate extends Aggregate {

    /** 父级ID */
    private final String parentId;

    public TreeAggregate(String id, Long version, Long deletedTime, String parentId) {
        super(id, version, deletedTime);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }
}

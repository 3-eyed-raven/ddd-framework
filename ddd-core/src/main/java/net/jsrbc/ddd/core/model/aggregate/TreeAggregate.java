package net.jsrbc.ddd.core.model.aggregate;

import net.jsrbc.ddd.core.model.valueobject.TreeInfo;
import java.util.Collections;
import java.util.Optional;

/**
 * 树形聚合，注意复写toString方法和实现Comparable接口
 * @author ZZZ on 2021/3/19 15:15
 * @version 1.0
 */
public abstract class TreeAggregate<T extends TreeAggregate<T>> extends Aggregate implements Comparable<T> {

    /** 树结构信息 */
    private final TreeInfo treeInfo;

    public TreeAggregate(String id, Long version, Long deleted, TreeInfo treeInfo) {
        super(id, version, deleted);
        this.treeInfo = Optional.ofNullable(treeInfo).orElse(new TreeInfo(Collections.emptyList(), null));
    }

    public TreeInfo getTreeInfo() {
        return treeInfo;
    }
}

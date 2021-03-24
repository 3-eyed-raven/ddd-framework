package net.jsrbc.ddd.core.view;

import net.jsrbc.ddd.core.model.valueobject.TreeInfo;

/**
 * 树形视图
 * @author ZZZ on 2021/3/19 16:16
 * @version 1.0
 */
public abstract class TreeView<T extends TreeView<T>> extends View implements Comparable<T> {

    /** 树结构信息 */
    private final TreeInfo treeInfo;

    public TreeView(String id, Long version, Long updateTime, TreeInfo treeInfo) {
        super(id, version, updateTime);
        this.treeInfo = treeInfo;
    }

    public TreeInfo getTreeInfo() {
        return treeInfo;
    }
}

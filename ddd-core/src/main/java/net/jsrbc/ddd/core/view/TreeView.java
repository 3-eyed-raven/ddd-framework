package net.jsrbc.ddd.core.view;

import java.util.List;

/**
 * 树形视图
 * @author ZZZ on 2021/3/19 16:16
 * @version 1.0
 */
public abstract class TreeView<T extends TreeView<T>> extends View implements Comparable<T> {

    /** 树结构信息 */
    private final String parentId;

    private final List<String> ancestorIds;

    public TreeView(String id, Long version, Long updateTime, String parentId, List<String> ancestorIds) {
        super(id, version, updateTime);
        this.parentId = parentId;
        this.ancestorIds = ancestorIds;
    }

    public String getParentId() {
        return parentId;
    }

    public List<String> getAncestorIds() {
        return ancestorIds;
    }
}

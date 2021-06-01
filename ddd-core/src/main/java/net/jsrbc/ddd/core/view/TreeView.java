package net.jsrbc.ddd.core.view;

import java.util.List;

/**
 * 树形视图
 * @author ZZZ on 2021/3/19 16:16
 * @version 1.0
 */
public abstract class TreeView<T extends TreeView<T>> extends View implements Comparable<T> {

    /** 父节点键名 */
    public final static String PARENT_KEY = "parentId";

    /** 祖先节点键名 */
    public final static String ANCESTOR_KEY = "ancestorIds";

    /** 树结构信息 */
    private String parentId;

    /** 祖先节点 */
    private List<String> ancestorIds;

    public TreeView(String id, Long version, Long updateTime, String parentId, List<String> ancestorIds) {
        super(id, version, updateTime);
        this.parentId = parentId;
        this.ancestorIds = ancestorIds;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(List<String> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }
}

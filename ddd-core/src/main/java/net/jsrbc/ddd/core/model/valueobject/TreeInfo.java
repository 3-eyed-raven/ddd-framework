package net.jsrbc.ddd.core.model.valueobject;

import java.util.List;

/**
 * 树节点信息值对象，祖先节点结构
 * @author ZZZ on 2020/8/5 16:56
 * @version 1.0
 */
public final class TreeInfo {

    /** 祖先节点ID */
    private final List<String> ancestorIds;

    /** 父节点ID */
    private final String parentId;

    public TreeInfo(List<String> ancestorIds, String parentId) {
        this.ancestorIds = ancestorIds;
        this.parentId = parentId;
    }

    public List<String> getAncestorIds() {
        return ancestorIds;
    }

    public String getParentId() {
        return parentId;
    }
}

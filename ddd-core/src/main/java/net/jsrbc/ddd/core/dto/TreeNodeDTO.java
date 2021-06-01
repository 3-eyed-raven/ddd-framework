package net.jsrbc.ddd.core.dto;

import java.util.List;

/**
 * 树结构传输数据
 * @author ZZZ on 2020/8/5 20:44
 * @version 1.0
 */
public class TreeNodeDTO {

    /** 节点的key */
    private final String key;

    /** 节点标题 */
    private final String title;

    /** 子节点 */
    private List<TreeNodeDTO> children;

    /** 实际数据 */
    private final Object payload;

    public TreeNodeDTO(String key, String title, Object payload) {
        this.key = key;
        this.title = title;
        this.payload = payload;
    }

    public void setChildren(List<TreeNodeDTO> children) {
        this.children = children;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public List<TreeNodeDTO> getChildren() {
        return children;
    }

    public Object getPayload() {
        return payload;
    }
}

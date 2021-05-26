package net.jsrbc.ddd.core.dto;

/**
 * 分页查询参数
 * @author ZZZ on 2021/5/26 11:37
 * @version 1.0
 */
public abstract class PageQueryDTO {

    /** 当前页，从1开始 */
    private final Integer current;

    /** 页面尺寸 */
    private final Integer pageSize;

    public PageQueryDTO(Integer current, Integer pageSize) {
        this.current = current;
        this.pageSize = pageSize;
    }

    public Integer getCurrent() {
        return current;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}

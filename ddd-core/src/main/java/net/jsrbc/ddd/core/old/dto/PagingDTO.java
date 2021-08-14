package net.jsrbc.ddd.core.old.dto;

import java.util.List;

/**
 * 分页数据
 * @author ZZZ on 2020/7/30 22:20
 * @version 1.0
 */
public class PagingDTO {

    /** 数据 */
    private final List<?> data;

    /** 当前页，从1开始 */
    private final Integer current;

    /** 页面尺寸 */
    private final Integer pageSize;

    /** 总数 */
    private final Long total;

    /** 是否查询成功 */
    private final Boolean success;

    public PagingDTO(List<?> data, Integer current, Integer pageSize, Long total, Boolean success) {
        this.data = data;
        this.current = current;
        this.pageSize = pageSize;
        this.total = total;
        this.success = success;
    }

    public List<?> getData() {
        return data;
    }

    public Integer getCurrent() {
        return current;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public Boolean getSuccess() {
        return success;
    }
}

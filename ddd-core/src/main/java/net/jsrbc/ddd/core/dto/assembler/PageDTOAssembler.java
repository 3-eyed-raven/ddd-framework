package net.jsrbc.ddd.core.dto.assembler;

import net.jsrbc.ddd.core.dto.PagingDTO;
import net.jsrbc.ddd.core.dto.PagingParameter;

import java.util.List;

/**
 * 分页数据装配
 * @author ZZZ on 2020/7/31 9:53
 * @version 1.0
 */
public final class PageDTOAssembler {
    /**
     * 转换为分页数据
     * @param pagingParameter 分页查询参数
     * @param data 查询出的数据
     * @param total 数据总量
     * @return 分页数据
     */
    public static PagingDTO toDTO(PagingParameter pagingParameter, List<?> data, long total) {
        if (pagingParameter.getCurrent() == null || pagingParameter.getPageSize() == null) {
            return new PagingDTO(data, 0, 0, 0L, false);
        } else {
            return new PagingDTO(data, pagingParameter.getCurrent(), pagingParameter.getPageSize(), total, true);
        }
    }

    /**
     * 不作分页的页面数据
     * @param data 数据
     * @return 页面数据
     */
    public static PagingDTO toDTO(List<?> data) {
        return new PagingDTO(data, null, null, null, true);
    }

    private PageDTOAssembler() {}
}

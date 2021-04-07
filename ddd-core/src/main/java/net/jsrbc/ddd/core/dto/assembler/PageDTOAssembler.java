package net.jsrbc.ddd.core.dto.assembler;

import net.jsrbc.ddd.core.dto.PageDTO;
import net.jsrbc.ddd.core.model.spec.Specification;

import java.util.List;

/**
 * 分页数据装配
 * @author ZZZ on 2020/7/31 9:53
 * @version 1.0
 */
public final class PageDTOAssembler {
    /**
     * 转换为分页数据
     * @param spec 查询规格
     * @param data 查询出的数据
     * @param total 数据总量
     * @return 分页数据
     */
    public static PageDTO toDTO(Specification spec, List<?> data, long total) {
        if (spec.getSkip() == null || spec.getSize() == null) {
            return new PageDTO(data, 0, 0, 0L, false);
        } else {
            return new PageDTO(data, spec.getSkip() / spec.getSize(), spec.getSize(), total, true);
        }
    }

    /**
     * 不作分页的页面数据
     * @param data 数据
     * @return 页面数据
     */
    public static PageDTO toDTO(List<?> data) {
        return new PageDTO(data, null, null, null, true);
    }

    private PageDTOAssembler() {}
}

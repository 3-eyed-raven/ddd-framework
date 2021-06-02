package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.utils.Validator;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 分页查询装配器
 * @author ZZZ on 2021/2/23 9:54
 * @version 1.0
 */
public final class PagingQueryAssembler {
    /**
     * 规格转查询对象
     * @param source 源查询条件
     * @param current 当前页，从1开始
     * @param pageSize 页面尺寸
     * @return 查询对象
     */
    public static Query toPagingQuery(Query source, Integer current, Integer pageSize) {
        Validator.notNull(current, "current 不能为空");
        Validator.notNull(pageSize, "pageSize 不能为空");
        return Query.of(source)
                .skip((long) (current - 1) * pageSize)
                .limit(pageSize);
    }

    private PagingQueryAssembler() {}
}

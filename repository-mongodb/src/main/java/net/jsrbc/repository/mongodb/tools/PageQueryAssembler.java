package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.utils.Validator;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 分页查询装配器
 * @author ZZZ on 2021/2/23 9:54
 * @version 1.0
 */
public final class PageQueryAssembler {

    /**
     * 规格转查询对象
     * @param criteria 条件
     * @param skip 跳过数量
     * @param limit 限制数量
     * @param orders 排序对象
     * @return 查询对象
     */
    public static Query toQuery(Criteria criteria, Integer skip, Integer limit, Sort.Order... orders) {
        Validator.notNull(skip, "skip不能为空");
        Validator.notNull(limit, "limit不能为空");
        Query query = new Query(criteria)
                .skip(skip)
                .limit(limit);
        if (orders != null && orders.length > 0) query.with(Sort.by(orders));
        return query;
    }

    private PageQueryAssembler() {}
}

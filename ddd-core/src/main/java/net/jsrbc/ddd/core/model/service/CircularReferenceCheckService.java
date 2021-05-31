package net.jsrbc.ddd.core.model.service;

import net.jsrbc.ddd.core.model.aggregate.TreeAggregate;
import net.jsrbc.ddd.core.model.repository.Repository;

import static net.jsrbc.ddd.core.utils.Validator.*;

/**
 * 循环引用检测服务
 * @author ZZZ on 2021/5/27 16:08
 * @version 1.0
 */
public final class CircularReferenceCheckService {
    /**
     * 检测是否存在循环引用
     * @param target 目标聚合
     * @param repository 仓库
     */
    public static void check(TreeAggregate target, Repository<? extends TreeAggregate> repository) {
        // 检测自引用
        neq(target.getId(), target.getParentId(), "自己不能作为自己的上级");
        // 检测循环引用
        String parentId = target.getParentId();
        while (parentId != null) {
            // 查找父级
            TreeAggregate pMenu = repository.findById(parentId);
            notNull(pMenu, "");
            parentId = pMenu.getParentId();
            neq(target.getId(), parentId, "父级不能为自己的子级");
        }
    }

    private CircularReferenceCheckService() {}
}

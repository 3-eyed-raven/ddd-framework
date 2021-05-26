package net.jsrbc.repository.mongodb.tools;

import static net.jsrbc.ddd.core.model.aggregate.Aggregate.VERSION_KEY;
import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

/**
 * 视图版本检查器
 * @author ZZZ on 2021/5/26 11:54
 * @version 1.0
 */
public class ViewVersionChecker {

    private final ViewMongoOperations viewMongoOperations;

    /**
     * 视图版本检查
     * @param id 视图ID
     * @param version 版本号
     * @param viewName 视图名称
     * @return 检查结果
     */
    public ViewVersionCheckResult check(String id, Long version, String viewName) {
        return new ViewVersionCheckResult(this.viewMongoOperations.exists(query(where("id").is(id).and(VERSION_KEY).gte(version)), viewName));
    }

    public ViewVersionChecker(ViewMongoOperations viewMongoOperations) {
        this.viewMongoOperations = viewMongoOperations;
    }
}

package net.jsrbc.repository.mongodb.tools;

import reactor.core.publisher.Mono;

import static net.jsrbc.ddd.core.model.aggregate.Aggregate.VERSION_KEY;
import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

/**
 * 响应式视图版本检查
 * @author ZZZ on 2021/5/26 11:54
 * @version 1.0
 */
public class ReactiveViewVersionChecker {

    private final ReactiveViewMongoOperations reactiveViewMongoOperations;

    /**
     * 视图版本检查
     * @param id 视图ID
     * @param version 版本号
     * @param viewName 视图名称
     * @return 检查结果
     */
    public Mono<ViewVersionCheckResult> check(String id, Long version, String viewName) {
        return this.reactiveViewMongoOperations
                .exists(query(where("id").is(id).and(VERSION_KEY).gte(version)), viewName)
                .map(ViewVersionCheckResult::new);
    }

    public ReactiveViewVersionChecker(ReactiveViewMongoOperations reactiveViewMongoOperations) {
        this.reactiveViewMongoOperations = reactiveViewMongoOperations;
    }
}

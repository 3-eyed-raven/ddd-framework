package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.dto.PageDTO;
import net.jsrbc.ddd.core.view.View;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 响应式视图mongodb模板
 * @author ZZZ on 2021/3/18 10:26
 * @version 1.0
 */
public class ReactiveViewMongoTemplate implements ReactiveViewMongoOperations {

    /** 版本控制键 */
    private final static String VERSION_KEY = "version";

    private final ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public Mono<Boolean> exists(Query query, String viewName) {
        return this.reactiveMongoOperations.exists(query, View.class, viewName);
    }

    @Override
    public <T extends View> Mono<PageDTO> findPagination(Criteria criteria, int current, int pageSize, Class<T> viewClass, Sort.Order... orders) {
        return this.reactiveMongoOperations
                .find(PageQueryAssembler.toQuery(criteria, current - 1, pageSize, orders), viewClass)
                .collectList()
                .zipWith(this.reactiveMongoOperations.count(new Query(criteria), viewClass))
                .map(t -> new PageDTO(t.getT1(), current, pageSize, t.getT2(), true));
    }

    @Override
    public <T extends View> Flux<T> find(Query query, Class<T> viewClass) {
        return this.reactiveMongoOperations.find(query, viewClass);
    }

    @Override
    public <T extends View> Mono<Long> count(Query query, Class<T> viewClass) {
        return this.reactiveMongoOperations.count(query, viewClass);
    }

    @Override
    public <T extends View> Mono<T> findOne(Query query, Class<T> viewClass) {
        return this.reactiveMongoOperations.findOne(query, viewClass);
    }

    @Override
    public Mono<Void> save(View view) {
        return this.reactiveMongoOperations
                .exists(query(where("id").is(view.getId())), view.getClass())
                .flatMap(b -> b
                        ? this.reactiveMongoOperations.findAndReplace(query(where("id").is(view.getId()).and(VERSION_KEY).lt(view.getVersion())), view)
                        : this.reactiveMongoOperations.insert(view))
                .then();
    }

    @Override
    public <T extends View> Mono<Void> remove(String id, Class<T> viewClass) {
        return this.reactiveMongoOperations.remove(query(where("id").is(id)), viewClass).then();
    }

    @Override
    public <T extends View> Mono<Void> remove(Query query, Class<T> viewClass) {
        return this.reactiveMongoOperations.remove(query, viewClass).then();
    }

    @Override
    public <T extends View> Mono<Void> update(Query query, Update update, Class<T> viewClass) {
        return this.reactiveMongoOperations.updateMulti(query, update, viewClass).then();
    }

    public ReactiveViewMongoTemplate(ReactiveMongoOperations reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
    }
}

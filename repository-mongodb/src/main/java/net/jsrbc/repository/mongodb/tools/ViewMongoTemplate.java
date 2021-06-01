package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.dto.PagingDTO;
import net.jsrbc.ddd.core.view.View;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.jsrbc.ddd.core.model.aggregate.Aggregate.VERSION_KEY;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 视图mongo操作
 * @author ZZZ on 2021/3/17 14:55
 * @version 1.0
 */
public class ViewMongoTemplate implements ViewMongoOperations {

    private final MongoOperations mongoOperations;

    @Override
    public <T extends View> boolean exists(Query query, String viewName) {
        return this.mongoOperations.exists(query, View.class, viewName);
    }

    @Override
    public <T extends View> List<T> find(Query query, Class<T> viewClass) {
        return this.mongoOperations.find(query, viewClass);
    }

    @Override
    public <T extends View> PagingDTO findPagination(Query query, int current, int pageSize, Class<T> viewClass) {
        List<T> data = this.mongoOperations.find(PagingQueryAssembler.toPagingQuery(query, current, pageSize), viewClass);
        long count = this.mongoOperations.count(query, viewClass);
        return new PagingDTO(data, current, pageSize, count, true);
    }

    @Override
    public <T extends View> long count(Query query, Class<T> viewClass) {
        return this.mongoOperations.count(query, viewClass);
    }

    @Override
    public <T extends View> void forEach(Query query, Class<T> viewClass, Consumer<T> consumer) {
        try (Stream<T> stream = this.mongoOperations.query(viewClass).matching(query).stream()) {
            stream.forEach(consumer);
        }
    }

    @Override
    public <T extends View> T findById(String id, Class<T> viewClass) {
        return this.mongoOperations.findById(id, viewClass);
    }

    @Override
    public <T extends View> T findOne(Query query, Class<T> viewClass) {
        return this.mongoOperations.findOne(query, viewClass);
    }

    @Override
    public void save(View view) {
        if (this.mongoOperations.exists(query(where("id").is(view.getId())), view.getClass())) {
            this.mongoOperations.findAndReplace(query(where("id").is(view.getId()).and(VERSION_KEY).lt(view.getVersion())), view);
        } else {
            this.mongoOperations.insert(view);
        }
    }

    @Override
    public <T extends View> void remove(String id, Class<T> viewClass) {
        this.mongoOperations.remove(query(where("id").is(id)), viewClass);
    }

    @Override
    public <T extends View> void remove(Query query, Class<T> viewClass) {
        this.mongoOperations.remove(query, viewClass);
    }

    @Override
    public <T extends View> void update(Query query, Update update, Class<T> viewClass) {
        this.mongoOperations.updateMulti(query, update, viewClass);
    }

    public ViewMongoTemplate(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}

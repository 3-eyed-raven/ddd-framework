package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.dto.PageDTO;
import net.jsrbc.ddd.core.view.View;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 视图mongo操作
 * @author ZZZ on 2021/3/17 14:55
 * @version 1.0
 */
public class ViewMongoTemplate implements ViewMongoOperations {

    /** 版本控制键 */
    private final static String VERSION_KEY = "version";

    private final MongoOperations mongoOperations;

    @Override
    public CheckResult exists(String id, Long version, String viewName) {
        boolean result = this.mongoOperations.exists(query(where("id").is(id).and(VERSION_KEY).gte(version)), View.class, viewName);
        return new CheckResult(result);
    }

    @Override
    public <T extends View> List<T> find(Query query, Class<T> viewClass) {
        return this.mongoOperations.find(query, viewClass);
    }

    @Override
    public <T extends View> PageDTO findPagination(Criteria criteria, int current, int pageSize, Class<T> viewClass, Sort.Order... orders) {
        List<T> data = this.mongoOperations.find(PageQueryAssembler.toQuery(criteria, current, pageSize, orders), viewClass);
        long count = this.mongoOperations.count(new Query(criteria), viewClass);
        return new PageDTO(data, current, pageSize, count, true);
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
    public <T extends View> void update(Query query, Update update, Class<T> viewClass) {
        this.mongoOperations.updateMulti(query, update, viewClass);
    }

    public ViewMongoTemplate(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}

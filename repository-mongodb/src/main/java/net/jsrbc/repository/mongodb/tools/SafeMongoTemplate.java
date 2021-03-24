package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 聚合的mongodb操作模板
 * @author ZZZ on 2021/2/23 10:20
 * @version 1.0
 */
public class SafeMongoTemplate implements SafeMongoOperations {

    /** 逻辑删除键 */
    private final static String DELETE_KEY = "deleted";

    /** 版本控制键 */
    private final static String VERSION_KEY = "version";

    /** mongo操作 */
    private final MongoOperations mongoOperations;

    @Override
    public <T extends Aggregate> List<T> find(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.find(query.addCriteria(where(DELETE_KEY).is(null)), aggregateClass);
    }

    @Override
    public <T extends Aggregate> List<T> findDeleted(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.find(query.addCriteria(where(DELETE_KEY).ne(null)), aggregateClass);
    }

    @Override
    public <T extends Aggregate> long count(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.count(query.addCriteria(where(DELETE_KEY).is(null)), aggregateClass);
    }

    @Override
    public <T extends Aggregate> long countDeleted(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.count(query.addCriteria(where(DELETE_KEY).ne(null)), aggregateClass);
    }

    @Override
    public <T extends Aggregate> void forEach(Query query, Class<T> aggregateClass, Consumer<T> consumer) {
        try (Stream<T> stream = this.mongoOperations.query(aggregateClass).matching(query.addCriteria(where(DELETE_KEY).is(null))).stream()) {
            stream.forEach(consumer);
        }
    }

    @Override
    public <T extends Aggregate> T findOne(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.findOne(query.addCriteria(where(DELETE_KEY).is(null)), aggregateClass);
    }

    @Override
    public <T extends Aggregate> T findOneDeleted(Query query, Class<T> aggregateClass) {
        return this.mongoOperations.findOne(query.addCriteria(where(DELETE_KEY).ne(null)), aggregateClass);
    }

    /**
     * 保存聚合，保存时会去校验版本号
     * @param aggregate 聚合
     * @throws InvalidDataException 版本冲突异常
     */
    @Override
    public void save(Aggregate aggregate) {
        // 执行新增或更新
        if (this.mongoOperations.exists(query(where("id").is(aggregate.getId())), aggregate.getClass())) {
            Aggregate result = this.mongoOperations.findAndReplace(
                    versionQuery(aggregate).addCriteria(where(DELETE_KEY).is(null)),
                    aggregate);
            if (result == null) {
                throw new InvalidDataException();
            }
        } else {
            this.mongoOperations.insert(aggregate);
        }
    }

    @Override
    public void remove(Aggregate aggregate) {
        Aggregate result = this.mongoOperations.findAndModify(
                versionQuery(aggregate).addCriteria(where(DELETE_KEY).is(null)),
                Update.update(DELETE_KEY, System.currentTimeMillis()),
                aggregate.getClass());
        if (result == null) {
            throw new InvalidDataException();
        }
    }

    @Override
    public void restore(Aggregate aggregate) {
        Aggregate result = this.mongoOperations.findAndModify(
                versionQuery(aggregate).addCriteria(where(DELETE_KEY).ne(null)),
                Update.update(DELETE_KEY, null),
                aggregate.getClass());
        if (result == null) {
            throw new InvalidDataException();
        }
    }

    public SafeMongoTemplate(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    /**
     * 版本查询
     * @param aggregate 聚合
     * @return 查询对象
     */
    private static Query versionQuery(Aggregate aggregate) {
        return query(where("id").is(aggregate.getId()).and(VERSION_KEY).is(aggregate.updateVersion()));
    }
}

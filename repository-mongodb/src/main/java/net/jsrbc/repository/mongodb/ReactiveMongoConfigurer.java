package net.jsrbc.repository.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import net.jsrbc.repository.mongodb.interpreter.IndexExpressionParser;
import net.jsrbc.repository.mongodb.tools.SSLHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 响应式mongodb配置
 * @author ZZZ on 2021/3/18 9:41
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class ReactiveMongoConfigurer extends AbstractReactiveMongoConfiguration {

    private final MongoProperties mongoProperties;

    @PostConstruct
    public void init() throws Exception {
        ReactiveMongoTemplate reactiveMongoTemplate = reactiveMongoTemplate(reactiveMongoDbFactory(),
                mappingMongoConverter(reactiveMongoDbFactory(),
                        customConversions(),
                        mongoMappingContext(customConversions())));
        // 创建集合
        ensureCollections(reactiveMongoTemplate, mongoProperties.getCollections());
        // 创建索引
        ensureIndex(reactiveMongoTemplate, mongoProperties.getIndexes());
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabaseName();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        SSLHelper.setMongoSSLEnvironment(this.mongoProperties);
        builder.applyConnectionString(new ConnectionString(mongoProperties.getConnectionString()));
    }

    public ReactiveMongoConfigurer(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    /**
     * 批量创建集合，不存在则创建
     * @param reactiveMongoOperations mongodb操作
     * @param collections 集合名称，多个用逗号分隔
     */
    private static void ensureCollections(ReactiveMongoOperations reactiveMongoOperations, String collections) {
        if (collections == null || collections.isEmpty()) return;
        Flux.fromArray(collections.split(","))
                .map(String::trim)
                .filterWhen(c -> reactiveMongoOperations.collectionExists(c).map(b -> !b))
                .flatMap(reactiveMongoOperations::createCollection)
                .blockLast();
    }

    /**
     * 批量创建索引,不存在则创建，字符串规则：
     * 0、只有属性字段，创建升序索引
     * 1、属性字段:asc|desc，创建升序或降序索引
     * 2、unique(属性字段:asc|desc)，创建唯一索引
     * 3、ttl:过期时间s|m|h|d(属性字段)，创建TTL索引
     * 复合索引用逗号分隔、不同索引用分号分隔
     * @param reactiveMongoOperations mongodb操作
     * @param map 索引map，key为集合名，value为索引模式字符串
     */
    private static void ensureIndex(ReactiveMongoOperations reactiveMongoOperations, Map<String, String> map) {
        if (map == null) return;
        Flux.fromIterable(map.entrySet())
                .map(en -> Tuples.of(en.getKey(), IndexExpressionParser.parse(en.getValue())))
                .flatMap(t -> Flux
                        .fromIterable(t.getT2())
                        .flatMap(expression -> reactiveMongoOperations
                                .indexOps(t.getT1())
                                .ensureIndex(expression.interpret(new Index()))))
                .blockLast();
    }
}

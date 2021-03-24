package net.jsrbc.repository.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import net.jsrbc.repository.mongodb.interpreter.IndexExpression;
import net.jsrbc.repository.mongodb.interpreter.IndexExpressionParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * mongodb数据库配置
 * @author ZZZ on 2021/2/22 20:47
 * @version 1.0
 */
@Configuration
@ConditionalOnMissingClass
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfigure extends AbstractMongoClientConfiguration {

    private final MongoProperties mongoProperties;

    @PostConstruct
    public void init() throws Exception {
        MongoTemplate mongoTemplate = mongoTemplate(mongoDbFactory(),
                mappingMongoConverter(mongoDbFactory(),
                        customConversions(),
                        mongoMappingContext(customConversions())));
        // 创建集合
        ensureCollections(mongoTemplate, mongoProperties.getCollections());
        // 创建索引
        ensureIndex(mongoTemplate, mongoProperties.getIndexes());
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabaseName();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongoProperties.getConnectionString()));
    }

    public MongoConfigure(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    /**
     * 批量创建集合，不存在则创建
     * @param mongoOperations mongodb操作
     * @param collections 集合名称，多个用逗号分隔
     */
    private static void ensureCollections(MongoOperations mongoOperations, String collections) {
        if (collections == null || collections.isEmpty()) return;
        Arrays.stream(collections.split(","))
                .map(String::trim)
                .filter(c -> !mongoOperations.collectionExists(c))
                .forEach(mongoOperations::createCollection);
    }

    /**
     * 批量创建索引,不存在则创建，字符串规则：
     * 0、只有属性字段，创建升序索引
     * 1、属性字段:asc|desc，创建升序或降序索引
     * 2、unique(属性字段:asc|desc)，创建唯一索引
     * 3、ttl:过期时间s|m|h|d(属性字段)，创建TTL索引
     * 复合索引用逗号分隔、不同索引用分号分隔
     * @param mongoOperations mongodb操作
     * @param map 索引map，key为集合名，value为索引模式字符串
     */
    private static void ensureIndex(MongoOperations mongoOperations, Map<String, String> map) {
        if (map == null) return;
        map.forEach((collection, indexStr) -> {
            List<IndexExpression> expressions = IndexExpressionParser.parse(indexStr);
            expressions.forEach(e -> mongoOperations.indexOps(collection).ensureIndex(e.interpret(new Index())));
        });
    }
}

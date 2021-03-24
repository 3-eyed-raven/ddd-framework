package net.jsrbc.repository.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

/**
 * mongodb数据库属性
 * @author ZZZ on 2020/6/22 16:12
 * @version 1.0
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "mongo")
public class MongoProperties {

    /** mongodb连接字符串 */
    private final String connectionString;

    /** 数据库名 */
    private final String databaseName;

    /** 需要创建的集合 */
    private final String collections;

    /** 需要的索引 */
    private final Map<String, String> indexes;

    public MongoProperties(String connectionString, String databaseName, String collections, Map<String, String> indexes) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.collections = collections;
        this.indexes = indexes;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollections() {
        return collections;
    }

    public Map<String, String> getIndexes() {
        return indexes;
    }
}

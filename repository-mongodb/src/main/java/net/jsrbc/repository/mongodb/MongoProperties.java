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

    /** 信任库 */
    private final String trustStore;

    /** 受信任的秘钥库 */
    private final String trustStorePassword;

    /** 秘钥库 */
    private final String keyStore;

    /** 秘钥库密码 */
    private final String keyStorePassword;

    /** 数据库名 */
    private final String databaseName;

    /** 需要创建的集合 */
    private final String collections;

    /** 需要的索引 */
    private final Map<String, String> indexes;

    public MongoProperties(String connectionString, String trustStore, String trustStorePassword,
                           String keyStore, String keyStorePassword, String databaseName,
                           String collections, Map<String, String> indexes) {
        this.connectionString = connectionString;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.databaseName = databaseName;
        this.collections = collections;
        this.indexes = indexes;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
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

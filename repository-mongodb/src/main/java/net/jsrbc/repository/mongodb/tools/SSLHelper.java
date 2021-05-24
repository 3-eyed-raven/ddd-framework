package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.repository.mongodb.MongoProperties;

/**
 * SSL虚拟机设置
 * @author ZZZ on 2021/5/24 19:41
 * @version 1.0
 */
public final class SSLHelper {

    private final static String TRUST_STORE = "javax.net.ssl.trustStore";

    private final static String TRUST_STORE_PWD = "javax.net.ssl.trustStorePassword";

    private final static String KEY_STORE = "javax.net.ssl.keyStore";

    private final static String KEY_STORE_PWD = "javax.net.ssl.keyStorePassword";

    /**
     * 设置Mongodb SSL环境
     * @param mongoProperties mongo属性
     */
    public static void setMongoSSLEnvironment(MongoProperties mongoProperties) {
        if (mongoProperties.getTrustStore() != null) {
            System.setProperty(TRUST_STORE, mongoProperties.getTrustStore());
        }
        if (mongoProperties.getTrustStorePassword() != null) {
            System.setProperty(TRUST_STORE_PWD, mongoProperties.getTrustStorePassword());
        }
        if (mongoProperties.getKeyStore() != null) {
            System.setProperty(KEY_STORE, mongoProperties.getKeyStore());
        }
        if (mongoProperties.getKeyStorePassword() != null) {
            System.setProperty(KEY_STORE_PWD, mongoProperties.getKeyStorePassword());
        }
    }

    private SSLHelper() {}
}

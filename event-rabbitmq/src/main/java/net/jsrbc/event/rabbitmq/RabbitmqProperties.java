package net.jsrbc.event.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * rabbitmq属性
 * @author ZZZ on 2021/2/24 16:01
 * @version 1.0
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitmqProperties {

    /** rabbitmq主机名 */
    private final String host;

    /** rabbitmq端口 */
    private final Integer port;

    /** 用户名 */
    private final String username;

    /** 密码 */
    private final String password;

    /** 虚拟主机 */
    private final String virtualHost;

    /** 通道池尺寸，为0或者不设置表示不启用 */
    private final Integer channelPoolSize;

    /** 交换机命名前缀 */
    private final String exchangePrefix;

    /** 队列命名前缀 */
    private final String queuePrefix;

    public RabbitmqProperties(String host, Integer port, String username, String password, String virtualHost, Integer channelPoolSize, String exchangePrefix, String queuePrefix) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.virtualHost = virtualHost == null ? "/" : virtualHost;
        this.channelPoolSize = channelPoolSize;
        this.exchangePrefix = exchangePrefix;
        this.queuePrefix = queuePrefix;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public Integer getChannelPoolSize() {
        return channelPoolSize;
    }

    public String getExchangePrefix() {
        return exchangePrefix;
    }

    public String getQueuePrefix() {
        return queuePrefix;
    }
}

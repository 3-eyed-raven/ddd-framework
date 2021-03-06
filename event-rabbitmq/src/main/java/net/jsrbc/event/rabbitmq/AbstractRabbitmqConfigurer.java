package net.jsrbc.event.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.jsrbc.ddd.core.event.EventSender;
import net.jsrbc.ddd.core.event.EventSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.rabbitmq.*;

/**
 * rabbitmq配置
 * @author ZZZ on 2021-03-24 15:53
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(RabbitmqProperties.class)
public abstract class AbstractRabbitmqConfigurer {

    @Autowired
    private RabbitmqProperties rabbitmqProperties;

    /**
     * 绑定事件和聚合，一个事件对应唯一的交换机和队列
     * 队列名：以前缀+事件类型命名
     * 交换机：以前缀+聚合类型命名，或者完全自定义命名
     * 路由key：以事件类型名命名
     * @param bindingRegistry 绑定注册
     */
    abstract protected void bind(BindingRegistry bindingRegistry);

    /**
     * 连接发布
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setHost(rabbitmqProperties.getHost());
        connectionFactory.setPort(rabbitmqProperties.getPort());
        connectionFactory.setUsername(rabbitmqProperties.getUsername());
        connectionFactory.setPassword(rabbitmqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitmqProperties.getVirtualHost());
        return connectionFactory;
    }

    /**
     * 连接提供
     * @return 连接提供者
     */
    @Bean
    public Utils.ExceptionFunction<ConnectionFactory, ? extends Connection> connectionSupplier(ConnectionFactory connectionFactory) {
        return Utils.singleConnectionSupplier(connectionFactory, ConnectionFactory::newConnection);
    }

    /**
     * 绑定注册
     */
    @Bean
    public BindingRegistry bindingRegistry() {
        BindingRegistry bindingRegistry = new BindingRegistry(rabbitmqProperties);
        bind(bindingRegistry);
        return bindingRegistry;
    }

    @Bean
    public Sender sender(Utils.ExceptionFunction<ConnectionFactory, ? extends Connection> connectionSupplier) {
        SenderOptions senderOptions = new SenderOptions();
        senderOptions.connectionSupplier(connectionSupplier);
        // 自定义sender
        configureSender(senderOptions);
        // 创建sender
        return RabbitFlux.createSender(senderOptions);
    }

    /**
     * 事件发送器
     */
    @Bean(destroyMethod = "close")
    public EventSender eventSender(BindingRegistry bindingRegistry, Sender sender) {
        // 声明交换机
        bindingRegistry.declareExchange(sender);
        // 包装后返回
        return new RabbitmqEventSender(sender, bindingRegistry);
    }

    /**
     * 事件订阅器
     */
    @Bean(destroyMethod = "close")
    @DependsOn("eventSender")
    public EventSubscriber eventSubscriber(Utils.ExceptionFunction<ConnectionFactory, ? extends Connection> connectionSupplier,
                                           BindingRegistry bindingRegistry, Sender sender) {
        ReceiverOptions receiverOptions = new ReceiverOptions();
        receiverOptions.connectionSupplier(connectionSupplier);
        // 自定义receiver
        configureReceiver(receiverOptions);
        // 创建receiver
        Receiver receiver = RabbitFlux.createReceiver(receiverOptions);
        // 包装后返回
        return new RabbitmqEventSubscriber(receiver, sender, bindingRegistry);
    }

    /**
     * 自定义发送选项
     * @param senderOptions 发送选项
     */
    protected void configureSender(SenderOptions senderOptions) {};

    /**
     * 自定义接收选项
     * @param receiverOptions 接收选项
     */
    protected void configureReceiver(ReceiverOptions receiverOptions) {}
}

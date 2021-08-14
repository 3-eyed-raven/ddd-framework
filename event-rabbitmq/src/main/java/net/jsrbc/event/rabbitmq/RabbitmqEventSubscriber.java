package net.jsrbc.event.rabbitmq;

import net.jsrbc.ddd.core.event.DomainEvent;
import net.jsrbc.ddd.core.event.EventSubscriber;
import net.jsrbc.ddd.core.old.utils.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件订阅器
 * @author ZZZ on 2021/2/25 14:26
 * @version 1.0
 */
public class RabbitmqEventSubscriber implements EventSubscriber {

    private final static Logger LOGGER = LogManager.getLogger();

    private final Receiver receiver;

    private final Sender sender;

    private final BindingRegistry bindingRegistry;

    /**
     * 订阅事件类型，事件消费完后才确认，prefetch默认设为250
     * 适合消息比较重要，需要确保消息处理完后才能确认，但是需要注意消息吞吐率
     * 订阅操作是异步执行的
     * @param eventClass 事件类型
     */
    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventClass, Consumer<T> eventConsumer) {
        declareAndBindQueue(eventClass)
                .flatMapMany(this.receiver::consumeManualAck)
                .subscribe(d -> {
                    T event = JsonUtils.toObject(new String(d.getBody(), StandardCharsets.UTF_8), eventClass);
                    try {
                        eventConsumer.accept(event);
                        d.ack();
                    } catch (Throwable e) {
                        LOGGER.error(e);
                    }
                });
    }

    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventClass, Consumer<T> eventConsumer, BiConsumer<? super Throwable, T> errorHandler) {
        declareAndBindQueue(eventClass)
                .flatMapMany(this.receiver::consumeAutoAck)
                .subscribe(d -> {
                    T event = JsonUtils.toObject(new String(d.getBody(), StandardCharsets.UTF_8), eventClass);
                    try {
                        eventConsumer.accept(event);
                    } catch (Throwable e) {
                        errorHandler.accept(e, event);
                    }
                });
    }

    @Override
    public <T extends DomainEvent> void globalSubscribe(Class<T> eventClass, Consumer<T> eventConsumer, BiConsumer<? super Throwable, T> errorHandler) {
        globalDeclareAndBindQueue(eventClass)
                .flatMapMany(this.receiver::consumeAutoAck)
                .subscribe(d -> {
                    T event = JsonUtils.toObject(new String(d.getBody(), StandardCharsets.UTF_8), eventClass);
                    try {
                        eventConsumer.accept(event);
                    } catch (Throwable e) {
                        errorHandler.accept(e, event);
                    }
                });
    }

    @Override
    public void close() {
        this.receiver.close();
    }

    public RabbitmqEventSubscriber(Receiver receiver, Sender sender, BindingRegistry bindingRegistry) {
        this.receiver = receiver;
        this.sender = sender;
        this.bindingRegistry = bindingRegistry;
    }

    /**
     * 声明和绑定队列
     * @param eventClass 事件类型
     * @return 队列
     */
    private Mono<String> declareAndBindQueue(Class<?> eventClass) {
        String routingKey = this.bindingRegistry.getRoutingKey(eventClass);
        String queueName = this.bindingRegistry.getQueueName(eventClass);
        String exchangeName = this.bindingRegistry.getExchangeName(eventClass);
        return this.sender
                .declareQueue(QueueSpecification.queue(queueName).durable(true))
                .flatMap(d -> this.sender
                        .bindQueue(BindingSpecification.queueBinding(exchangeName, routingKey, d.getQueue()))
                        .thenReturn(d.getQueue()));
    }

    /**
     * 声明和绑定队列
     * @param eventClass 事件类型
     * @return 队列
     */
    private Mono<String> globalDeclareAndBindQueue(Class<?> eventClass) {
        String routingKey = this.bindingRegistry.getRoutingKey(eventClass);
        String exchangeName = this.bindingRegistry.getExchangeName(eventClass);
        return this.sender
                .declareQueue(QueueSpecification.queue())
                .flatMap(d -> this.sender
                        .bindQueue(BindingSpecification.queueBinding(exchangeName, routingKey, d.getQueue()))
                        .thenReturn(d.getQueue()));
    }
}

package net.jsrbc.ddd.core.model.event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件接收者
 * @author ZZZ on 2021-03-24 14:48
 * @version 1.0
 */
public interface EventSubscriber extends AutoCloseable {

    /**
     * 订阅事件事件，消费遇到错误时不确认该事件，即队列下次还会继续发送
     * @param eventClass 事件类型
     * @param eventConsumer 事件消费
     */
    <T extends DomainEvent> void subscribe(Class<T> eventClass, Consumer<T> eventConsumer);

    /**
     * 订阅事件事件，接收到事件即完成确认
     * @param eventClass 事件类型
     * @param eventConsumer 事件消费
     * @param errorHandler 错误处理
     */
    <T extends DomainEvent> void subscribe(Class<T> eventClass, Consumer<T> eventConsumer, BiConsumer<? super Throwable, T> errorHandler);
}
